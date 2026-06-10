package com.example.aiplaygroundtwo.feature.approvaldetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
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
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
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
                contentWindowInsets = WindowInsets(0, 0, 0, 0),
                topBar = {
                    ApprovalDetailTopBar(title = uiState.request.title, onBack = onBack)
                },
                bottomBar = {
                    if (!uiState.isAlreadyResolved) {
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
                    }
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
                    if (uiState.isAlreadyResolved) {
                        AlreadyResolvedBanner()
                    }
                    RequestDetailHeader(
                        request = uiState.request,
                        jobTitle = uiState.jobTitle,
                        repoName = uiState.repoName,
                    )
                    val readOnly = uiState.isAlreadyResolved
                    when (uiState.request.type) {
                        RequestType.Approval -> ApprovalBody(
                            request = uiState.request,
                            feedback = feedback,
                            readOnly = readOnly,
                            onFeedbackChange = { value ->
                                if (value.length <= MAX_FEEDBACK_LENGTH) {
                                    feedback = value
                                }
                            },
                        )
                        RequestType.NeedsInput -> NeedsInputBody(
                            request = uiState.request,
                            selectedOption = selectedOption,
                            readOnly = readOnly,
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

@Composable
private fun AlreadyResolvedBanner(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.primaryContainer,
        shape = MaterialTheme.shapes.medium,
    ) {
        Text(
            text = "Already resolved",
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
        )
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
    readOnly: Boolean,
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
        if (!readOnly) {
            FeedbackField(
                label = "Feedback (optional)",
                value = feedback,
                onValueChange = onFeedbackChange,
            )
        }
    }
}

@Composable
private fun NeedsInputBody(
    request: ReviewRequest,
    selectedOption: String?,
    readOnly: Boolean,
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
                        .then(
                            if (!readOnly) {
                                Modifier.selectable(
                                    selected = selectedOption == option,
                                    onClick = { onOptionSelected(option) },
                                    role = Role.RadioButton,
                                )
                            } else {
                                Modifier
                            },
                        )
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    RadioButton(
                        selected = selectedOption == option,
                        onClick = { if (!readOnly) onOptionSelected(option) },
                        enabled = !readOnly,
                    )
                    Text(
                        text = option,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(start = 8.dp),
                    )
                }
            }
        }
        if (!readOnly) {
            FeedbackField(
                label = "Additional guidance (optional)",
                value = feedback,
                onValueChange = onFeedbackChange,
            )
        }
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
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 3.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            when (requestType) {
                RequestType.Approval -> {
                    OutlinedButton(
                        onClick = onReject,
                        enabled = canReject,
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = AgentRed),
                    ) {
                        ActionButtonLabel(text = "Reject")
                    }
                    Button(
                        onClick = onApprove,
                        enabled = !isSubmitting,
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp),
                    ) {
                        ActionButtonLabel(text = "Approve")
                    }
                }
                RequestType.NeedsInput -> {
                    OutlinedButton(
                        onClick = onReject,
                        enabled = canReject,
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp),
                    ) {
                        ActionButtonLabel(text = "Pause / Reject")
                    }
                    Button(
                        onClick = onContinue,
                        enabled = canContinue,
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp),
                    ) {
                        ActionButtonLabel(text = "Continue with selection")
                    }
                }
            }
        }
    }
}

@Composable
private fun ActionButtonLabel(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        modifier = modifier.fillMaxWidth(),
        style = MaterialTheme.typography.labelLarge,
        textAlign = TextAlign.Center,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
    )
}
