import io.github.mvillafuertem.Utils
import io.github.mvillafuertem.Utilities

def call(String name = 'human') {
    // Any valid steps can be called from this code, just like in other
    // Scripted Pipeline
    def stdout = sh script:'ls', returnStdout: true
    Utils utils = new Utils()
    //no funcionaría bajo JenkinsPipelineUnit --> utils.mish()

    Utilities utilities = new Utilities(this)
    utilities.mish()

    println "Hello, $name. $stdout"
}

return this
