package io.github.mvillafuertem

final class Utilities implements Serializable {
    def steps
    Utilities(steps) {this.steps = steps}

    def mish(args) {
        steps.sh([script:'ls -a', returnStdout: true])
    }
}