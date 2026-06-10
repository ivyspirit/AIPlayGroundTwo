package com.example.aiplaygroundtwo.feature.approvaldetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.example.aiplaygroundtwo.domain.model.RequestType
import com.example.aiplaygroundtwo.domain.model.ReviewRequest
import com.example.aiplaygroundtwo.ui.components.AgentLoadingState
import com.example.aiplaygroundtwo.ui.placeholder.BackIcon
import com.example.aiplaygroundtwo.ui.theme.AgentRed

private const val MAX_FEEDBACK_LENGTH = 500

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApprovalDetailScreen(
    uiState: ApprovalDetailUiState,
    onBack: () -> Unit,
    onApprove: (String) -> Unit,
    onReject: (String) -> Unit,
    onContinue: (String, String) -> Unit,
    modifier: Modifier = Modifier,
) {
    when (uiState) {
        ApprovalDetailUiState.Loading -> {
            Scaffold(
                modifier = modifier,
                topBar = {
                    ApprovalDetailTopBar(title = "Request", onBack = onBack)
                },
            ) { innerPadding ->
                AgentLoadingState(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                )
            }
        }
        is ApprovalDetailUiState.Error -> {
            Scaffold(
                modifier = modifier,
                topBar = {
                    ApprovalDetailTopBar(title = "Request", onBack = onBack)
                },
            ) { innerPadding ->
                Text(
                    text = uiState.message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(24.dp),
                )
            }
        }
        is ApprovalDetailUiState.Content -> {
            var feedback by rememberSaveable { mutableStateOf("") }
            var selectedOption by rememberSaveable { mutableStateOf<String?>(null) }
            Scaffold(
                modifier = modifier,
                topBar = {
                    ApprovalDetailTopBar(title = uiState.request.title, onBack = onBack)
                },
                bottomBar = {
                    ApprovalDetailBottomBar(
                        requestType = uiState.request.type,
                        canContinue = selectedOption != null && !uiState.isSubmitting,
                        canReject = when (uiState.request.type) {
                            RequestType.NeedsInput -> feedback.isNotBlank() && !uiState.isSubmitting
                            RequestType.Approval -> !uiState.isSubmitting
                        },
                        isSubmitting = uiState.isSubmitting,
                        onReject = { onReject(feedback) },
                        onApprove = { onApprove(feedback) },
                        onContinue = {
                            val option = selectedOption
                            if (option != null) {
                                onContinue(option, feedback)
                            }
                        },
                    )
                },
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    RequestDetailHeader(
                        request = uiState.request,
                        jobTitle = uiState.jobTitle,
                        repoName = uiState.repoName,
                    )
                    when (uiState.request.type) {
                        RequestType.Approval -> ApprovalBody(
                            request = uiState.request,
                            feedback = feedback,
                            onFeedbackChange = { value ->
                                if (value.length <= MAX_FEEDBACK_LENGTH) {
                                    feedback = value
                                }
                            },
                        )
                        RequestType.NeedsInput -> NeedsInputBody(
                            request = uiState.request,
                            selectedOption = selectedOption,
                            onOptionSelected = { selectedOption = it },
                            feedback = feedback,
                            onFeedbackChange = { value ->
                                if (value.length <= MAX_FEEDBACK_LENGTH) {
                                    feedback = value
                                }
                            },
                        )
                    }
                    uiState.submitError?.let { error ->
                        Text(
                            text = error,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ApprovalDetailTopBar(
    title: String,
    onBack: () -> Unit,
) {
    TopAppBar(
        title = { Text(title) },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(imageVector = BackIcon, contentDescription = "Back")
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
        ),
    )
}

@Composable
private fun ApprovalBody(
    request: ReviewRequest,
    feedback: String,
    onFeedbackChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        SectionBlock(
            title = "Proposed action",
            body = request.proposedAction.orEmpty(),
        )
        SectionBlock(
            title = "Why",
            body = request.reasoning,
        )
        request.affectedFiles?.let { files ->
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(text = "Affected files", style = MaterialTheme.typography.titleSmall)
                files.forEach { path ->
                    Text(
                        text = path,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
        FeedbackField(
            label = "Feedback (optional)",
            value = feedback,
            onValueChange = onFeedbackChange,
        )
    }
}

@Composable
private fun NeedsInputBody(
    request: ReviewRequest,
    selectedOption: String?,
    onOptionSelected: (String) -> Unit,
    feedback: String,
    onFeedbackChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        SectionBlock(
            title = "Question",
            body = request.question.orEmpty(),
        )
        SectionBlock(
            title = "Why",
            body = request.reasoning,
        )
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(text = "Your selection", style = MaterialTheme.typography.titleSmall)
            request.options?.forEach { option ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = selectedOption == option,
                            onClick = { onOptionSelected(option) },
                            role = Role.RadioButton,
                        )
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    RadioButton(
                        selected = selectedOption == option,
                        onClick = { onOptionSelected(option) },
                    )
                    Text(
                        text = option,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(start = 8.dp),
                    )
                }
            }
        }
        FeedbackField(
            label = "Additional guidance (optional)",
            value = feedback,
            onValueChange = onFeedbackChange,
        )
    }
}

@Composable
private fun SectionBlock(
    title: String,
    body: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(text = title, style = MaterialTheme.typography.titleSmall)
        Text(
            text = body,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun FeedbackField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3,
        )
        Text(
            text = "${value.length}/$MAX_FEEDBACK_LENGTH",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun ApprovalDetailBottomBar(
    requestType: RequestType,
    canContinue: Boolean,
    canReject: Boolean,
    isSubmitting: Boolean,
    onReject: () -> Unit,
    onApprove: () -> Unit,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        when (requestType) {
            RequestType.Approval -> {
                OutlinedButton(
                    onClick = onReject,
                    enabled = canReject,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = AgentRed),
                ) {
                    Text("Reject")
                }
                Button(
                    onClick = onApprove,
                    enabled = !isSubmitting,
                    modifier = Modifier.weight(1f),
                ) {
                    Text("Approve")
                }
            }
            RequestType.NeedsInput -> {
                OutlinedButton(
                    onClick = onReject,
                    enabled = canReject,
                    modifier = Modifier.weight(1f),
                ) {
                    Text("Pause / Reject")
                }
                Button(
                    onClick = onContinue,
                    enabled = canContinue,
                    modifier = Modifier.weight(1f),
                ) {
                    Text("Continue with selection")
                }
            }
        }
    }
}
