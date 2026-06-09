package com.example.aiplaygroundtwo.navigation

object AppDestinations {
    const val DASHBOARD = "dashboard"
    const val JOB_DETAIL = "job_detail/{jobId}"
    const val REQUESTS_CENTER = "requests_center"
    const val APPROVAL_DETAIL = "approval_detail/{requestId}"

    const val JOB_ID_ARG = "jobId"
    const val REQUEST_ID_ARG = "requestId"

    fun jobDetail(jobId: String): String = "job_detail/$jobId"
    fun approvalDetail(requestId: String): String = "approval_detail/$requestId"

    fun showsBottomNav(route: String?): Boolean {
        if (route == null) return true
        return !route.startsWith("approval_detail/")
    }
}
