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


    def subject = "${buildStatus}: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}] (<${env.RUN_DISPLAY_URL}|Open>) (<${env.RUN_CHANGES_DISPLAY_URL}|  Changes>)'"
    def branchName = "${env.BRANCH_NAME}"
    def commit = sh(returnStdout: true, script: 'git rev-parse HEAD')
    def author = sh(returnStdout: true, script: "git --no-pager show -s --format='%an'").trim()
    def message = sh(returnStdout: true, script: 'git log -1 --pretty=%B').trim()

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
    def testSummaryRaw = getTestSummary()
    // format test summary as a code block
    def testSummary = "`${testSummaryRaw}`"
    println testSummary.toString()


    // JSONObject for branch
    JSONObject branch = createBranch(branchName)
    // JSONObject for author
    JSONObject commitAuthor = createCommitAuthor(author)
    // JSONObject for branch
    JSONObject commitMessage = createCommitMessage(message)
    // JSONObject for test results
    JSONObject testResults = createTestSummary(testSummary)
    attachment.put('fields', [branch, commitAuthor, commitMessage, testResults])
    JSONArray attachments = createAttachments()

    // Send notifications
    slackSend(color: colorCode, message: subject, attachments: attachments.toString(), channel: channel)

}

private static JSONObject createBranch(groovy.lang.GString branchName) {
    JSONObject branch = new JSONObject()
    branch.put('title', 'Branch')
    branch.put('value', branchName.toString())
    branch.put('short', true)
    branch
}

private static JSONObject createCommitAuthor(author) {
    JSONObject commitAuthor = new JSONObject()
    commitAuthor.put('title', 'Author')
    commitAuthor.put('value', author.toString())
    commitAuthor.put('short', true)
    commitAuthor
}

private static JSONObject createCommitMessage(message) {
    JSONObject commitMessage = new JSONObject()
    commitMessage.put('title', 'Commit Message')
    commitMessage.put('value', message.toString())
    commitMessage.put('short', false)
    commitMessage
}

private static JSONObject createTestSummary(groovy.lang.GString testSummary) {
    JSONObject testResults = new JSONObject()
    testResults.put('title', 'Test Summary')
    testResults.put('value', testSummary.toString())
    testResults.put('short', false)
    testResults
}

private JSONArray createAttachments() {

    def title = "${env.JOB_NAME} Build: ${env.BUILD_NUMBER}"
    def title_link = "${env.RUN_DISPLAY_URL}"


    JSONObject attachment = new JSONObject()
    attachment.put('author', "jenkins")
    attachment.put('author_link', "https://danielschaaff.com")
    attachment.put('title', title.toString())
    attachment.put('title_link', title_link.toString())
    attachment.put('text', subject.toString())
    attachment.put('fallback', "fallback message")
    attachment.put('color', colorCode)
    attachment.put('mrkdwn_in', ["fields"])

    JSONArray attachments = new JSONArray()
    attachments.add(attachment)
    println attachments.toString()
    return attachments
}