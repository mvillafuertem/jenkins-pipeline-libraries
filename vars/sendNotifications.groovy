#!/usr/bin/env groovy
import hudson.tasks.test.AbstractTestResultAction
import net.sf.json.JSONArray
import net.sf.json.JSONObject

/**
 * notify slack and set message based on build status
 */
def call(String buildStatus = 'STARTED', String channel = '#resine') {

    // buildStatus of null means successfull
    buildStatus = buildStatus ?: 'SUCCESSFUL'
    channel = channel ?: '#resine'


    // Default values
    colorName = 'RED'
    colorCode = '#FF0000'
    subject = "${buildStatus}: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}] (<${env.RUN_DISPLAY_URL}|Open>) (<${env.RUN_CHANGES_DISPLAY_URL}|  Changes>)'"
    title = "${env.JOB_NAME} Build: ${env.BUILD_NUMBER}"
    title_link = "${env.RUN_DISPLAY_URL}"

    // Override default values based on build status
    if (buildStatus == 'STARTED') {
        color = 'YELLOW'
        colorCode = '#FFFF00'
    } else if (buildStatus == 'SUCCESSFUL') {
        color = 'GREEN'
        colorCode = 'good'
    } else if (buildStatus == 'UNSTABLE') {
        color = 'YELLOW'
        colorCode = 'warning'
    } else {
        color = 'RED'
        colorCode = 'danger'
    }

    JSONObject attachment = createAttachment(title, title_link, subject, colorCode)
    JSONArray attachments = createAttachments(attachment)

    // Send notifications
    slackSend(color: colorCode, message: subject, attachments: attachments.toString(), channel: channel)

}

private static JSONArray createAttachments(JSONObject attachment) {
    final JSONArray attachments = new JSONArray()
    attachments.add(attachment)
    attachments
}

private static JSONObject createTestSummary() {
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

private static JSONObject createCommitMessage() {
    def commit = sh(returnStdout: true, script: 'git rev-parse HEAD')
    def message = sh(returnStdout: true, script: 'git log -1 --pretty=%B').trim()

    final JSONObject commitMessage = new JSONObject()
    commitMessage.put('title', "Commit Message $commit")
    commitMessage.put('value', message.toString())
    commitMessage.put('short', false)
    commitMessage
}

private static JSONObject createCommitAuthor() {
    def author = sh(returnStdout: true, script: "git --no-pager show -s --format='%an'").trim()
    final JSONObject commitAuthor = new JSONObject()
    commitAuthor.put('title', 'Author')
    commitAuthor.put('value', author.toString())
    commitAuthor.put('short', true)
    commitAuthor
}

private static JSONObject createBranch() {

    branchName = "${env.BRANCH_NAME}"

    final JSONObject branch = new JSONObject()
    branch.put('title', 'Branch')
    branch.put('value', branchName.toString())
    branch.put('short', true)
    return branch
}

private static JSONObject createAttachment(GString title, GString title_link, GString subject, java.lang.String colorCode) {

    final JSONObject attachment = new JSONObject()
    attachment.put('author', "mvillafuerte")
    attachment.put('author_link', "https://mvillafuerte.com")
    attachment.put('title', title.toString())
    attachment.put('title_link', title_link.toString())
    attachment.put('text', subject.toString())
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