package com.chizi.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
class HelloPlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {
        project.extensions.create('hello', MyExtension)
        project.task('hello') << {
            MyExtension ext = project.extensions.hello;
            if (ext.enable) {
                println "Hello ${ext.text}!"
            } else {
                println 'HelloPlugin is disabled.'
            }
        }
    }
}