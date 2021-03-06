#!/usr/bin/env groovy
import hudson.tasks.test.AbstractTestResultAction
import net.sf.json.JSONArray
import net.sf.json.JSONObject

/**
 * notify slack and set message based on build status
 */
def call(String buildStatus = 'STARTED', String channel = '#jenkins') {

    // buildStatus of null means successfull
    buildStatus = buildStatus ?: 'SUCCESSFUL'
    channel = channel ?: '#jenkins'

    // Default values
    //subject = "${buildStatus}: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}] (<${env.RUN_DISPLAY_URL}|Open>) (<${env.RUN_CHANGES_DISPLAY_URL}|  Changes>)'"
    subject = "${buildStatus}: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}] (<${env.RUN_DISPLAY_URL}|Open>) (<${env.RUN_CHANGES_DISPLAY_URL}|  Changes>)'"
    title = "${env.JOB_NAME} Build: #${env.BUILD_NUMBER}"
    title_link = "${env.RUN_DISPLAY_URL}"

    if (buildStatus == 'STARTED') {
        colorCode = '#FFFF00'
    } else if (buildStatus == 'SUCCESSFUL') {
        subject = ":tada: ${buildStatus} :rocket:"
        colorCode = 'good'
    } else if (buildStatus == 'ABORTED') {
        subject = ":warning: ${buildStatus} :bomb:"
        colorCode = 'warning'
    } else if (buildStatus == 'UNSTABLE') {
        subject = ":warning: ${buildStatus} :bomb:"
        colorCode = 'warning'
    } else {
        subject = ":boom: ${buildStatus} :fire:"
        colorCode = 'danger'
    }

    final JSONObject attachment = createAttachment(title, title_link, colorCode)
    final JSONArray attachments = createAttachments(attachment)

    // Send notifications
    slackSend(color: colorCode, message: subject, attachments: attachments.toString(), channel: channel)

}

private JSONArray createAttachments(JSONObject attachment) {
    final JSONArray attachments = new JSONArray()
    attachments.add(attachment)
    attachments
}

private JSONObject createTestSummary() {
    // get test results for slack message
    @NonCPS
    def getTestSummary = { ->
        def testResultAction = currentBuild.rawBuild.getAction(AbstractTestResultAction.class)
        def summary = ""

        if (testResultAction != null) {
            def total = testResultAction.getTotalCount()
            def failed = testResultAction.getFailCount()
            def skipped = testResultAction.getSkipCount()

            summary = "Test results:\n\t"
            summary = summary + ("Passed: " + (total - failed - skipped))
            summary = summary + (", Failed: " + failed + " ${testResultAction.failureDiffString}")
            summary = summary + (", Skipped: " + skipped)
        } else {
            summary = "No tests found"
        }
        return summary
    }
    testSummaryRaw = getTestSummary()
    testSummary = "`${testSummaryRaw}`"

    final JSONObject testResults = new JSONObject()
    testResults.put('title', 'Test Summary')
    testResults.put('value', testSummary.toString())
    testResults.put('short', false)
    testResults
}

private JSONObject createCommitMessage() {
    def commit = sh(returnStdout: true, script: 'git rev-parse HEAD')
    def message = sh(returnStdout: true, script: 'git log -1 --pretty=%B').trim()
    commitMess = "${env.RUN_CHANGES_DISPLAY_URL}"

    final JSONObject commitMessage = new JSONObject()
    commitMessage.put('title', "Commit Message")
    commitMessage.put('value', message.toString() + "\n\n<" + commitMess + "| Changes>")
    commitMessage.put('short', false)
    commitMessage
}

private JSONObject createCommitAuthor() {
    def author = sh(returnStdout: true, script: "git --no-pager show -s --format='%an'").trim()
    final JSONObject commitAuthor = new JSONObject()
    commitAuthor.put('title', 'Author')
    commitAuthor.put('value', author.toString())
    commitAuthor.put('short', true)
    commitAuthor
}

private JSONObject createBranch() {

    branchName = "${env.BRANCH_NAME}"

    final JSONObject branch = new JSONObject()
    branch.put('title', 'Branch')
    branch.put('value', branchName.toString())
    branch.put('short', true)
    return branch
}

private JSONObject createAttachment(GString title, GString title_link, java.lang.String colorCode) {

    final JSONObject attachment = new JSONObject()
    attachment.put('author', "mvillafuerte")
    attachment.put('author_link', "https://mvillafuerte.com")
    attachment.put('title', title.toString())
    attachment.put('title_link', title_link.toString())
    //attachment.put('text', subject.toString())
    attachment.put('fallback', "fallback message")
    attachment.put('color', colorCode)
    attachment.put('mrkdwn_in', ["fields"])

    final JSONObject branch = createBranch()
    final JSONObject commitAuthor = createCommitAuthor()
    final JSONObject commitMessage = createCommitMessage()
    final JSONObject testResults = createTestSummary()

    attachment.put('fields', [branch, commitAuthor, commitMessage, testResults])

    return attachment
}

return this