package com.example.aiplaygroundtwo.ui.placeholder

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.aiplaygroundtwo.ui.theme.AIPlayGroundTwoTheme

@Preview
@Composable
private fun JobDetailPlaceholderScreenPreview() {
    AIPlayGroundTwoTheme {
        JobDetailPlaceholderScreen(
            jobId = "job-1",
            onBack = {},
            onOpenApproval = {},
        )
    }
}

@Preview
@Composable
private fun RequestsCenterPlaceholderScreenPreview() {
    AIPlayGroundTwoTheme {
        RequestsCenterPlaceholderScreen(
            onBack = {},
            onOpenApproval = {},
        )
    }
}

@Preview
@Composable
private fun ApprovalDetailPlaceholderScreenPreview() {
    AIPlayGroundTwoTheme {
        ApprovalDetailPlaceholderScreen(
            requestId = "request-coder-approval",
            onBack = {},
        )
    }
}
