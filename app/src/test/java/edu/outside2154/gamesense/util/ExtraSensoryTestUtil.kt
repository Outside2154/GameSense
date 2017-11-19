package edu.outside2154.gamesense.util

import java.util.*

class ExtraSensoryUserTestImpl(
        override val uuid: String,
        override val files: List<ExtraSensoryFile>
) : ExtraSensoryUser

class ExtraSensoryFileTestImpl(
        override val creationTime: Date,
        override val isServer: Boolean,
        override val info: ExtraSensoryInfo?
) : ExtraSensoryFile