package edu.outside2154.gamesense.model

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

const val EPS = 1.0e-008

class ModelBossTest {
    private lateinit var boss: Boss

    @Before
    fun setUp() {
        boss = BossLocalImpl(BOSS_BASE_HEALTH, BOSS_BASE_ATTACK, 1)
    }

    @Test
    fun testTakeDamage() {
        boss.takeDamage(5.0)
        assertEquals(BOSS_BASE_HEALTH - 5.0, boss.health, EPS)
        boss.takeDamage(27.5)
        assertEquals(BOSS_BASE_HEALTH - 32.5, boss.health, EPS)
        boss.takeDamage(1.5)
        assertEquals(BOSS_BASE_HEALTH - 34.0, boss.health, EPS)
        boss.takeDamage(0.0)
        assertEquals(BOSS_BASE_HEALTH - 34.0, boss.health, EPS)
        boss.takeDamage(70.0)
        assertEquals(0.0, boss.health, EPS)
    }

    @Test
    fun testDead() {
        boss.takeDamage(0.0)
        assertEquals(false, boss.dead)
        boss.takeDamage(99.9)
        assertEquals(false, boss.dead)
        boss.takeDamage(BOSS_BASE_HEALTH)
        assertEquals(true, boss.dead)
    }

    @Test
    fun testReset() {
        boss.reset(false)
        assertEquals(1, boss.lvl)
        assertEquals(BOSS_BASE_HEALTH, boss.health, EPS)
        assertEquals(BOSS_BASE_ATTACK, boss.attack, EPS)
        boss.reset(true)
        assertEquals(2, boss.lvl)
        assertEquals(BOSS_BASE_HEALTH + 50.0, boss.health, EPS)
        assertEquals(BOSS_BASE_ATTACK + 5.0, boss.attack, EPS)
        boss.reset(true)
        assertEquals(3, boss.lvl)
        assertEquals(BOSS_BASE_HEALTH + 100.0, boss.health, EPS)
        assertEquals(BOSS_BASE_ATTACK + 10.0, boss.attack, EPS)
    }
}

class ModelPlayerTest {
    private lateinit var player: Player
    private lateinit var boss: Boss

    @Before
    fun setUp() {
        val intStat = Stat(mapOf("Lab work" to 4.0, "In class" to 8.0), mapOf("Lab work" to 2.0, "In class" to 4.0))
        val atkStat = Stat(mapOf("Running" to 3.0, "Exercise" to 7.0), mapOf("Running" to 1.5, "Exercise" to 1.0))
        val regenStat = Stat(mapOf("Sleeping" to 56.0), mapOf("Sleeping" to 14.0))
        player = PlayerLocalImpl(regenStat, atkStat, intStat, PLAYER_BASE_HEALTH, 0)
        boss = BossLocalImpl(BOSS_BASE_HEALTH, BOSS_BASE_ATTACK, 1)
    }

    @Test
    fun testFight() {
        assertEquals(0.5, player.intStat.calcStat() ?: 0.0, EPS)
        assertEquals(0.25, player.atkStat.calcStat() ?: 0.0, EPS)
        assertEquals(0.25, player.regenStat.calcStat() ?: 0.0, EPS)
        assertEquals(25.0, player.pureDamage, EPS)

        val delta = player.pureDamage * (PLAYER_CRIT_MULT - 1)

        player.fight(boss)
        assertEquals(false, player.dead)
        assertEquals(false, boss.dead)
        assertEquals(PLAYER_BASE_HEALTH - BOSS_BASE_ATTACK, player.health, EPS)
        assertEquals(BOSS_BASE_HEALTH - player.pureDamage, boss.health, delta)
        player.fight(boss)
        assertEquals(false, player.dead)
        assertEquals(BOSS_BASE_HEALTH - 2.0 * player.pureDamage, boss.health, 2.0 * delta)
    }
}

class ModelStatTest {
    private lateinit var stat: Stat

    @Before
    fun setUp() {
        stat = Stat(mapOf("Lab work" to 4.0, "In class" to 8.0), mapOf("Lab work" to 0.0, "In class" to 0.0))
    }

    @Test
    fun testUpdateCurrent() {
        val goals1 = Stat.StatItems(mapOf("Lab work" to 4.0, "In class" to 8.0))
        assertEquals(goals1, stat.goals)

        val cur1 = Stat.StatItems(mapOf("Lab work" to 0.0, "In class" to 0.0))
        stat.updateCurrent(mapOf())
        stat.updateCurrent(mapOf("Running" to 3.0, "Exercise" to 6.0))
        assertEquals(cur1, stat.current)

        val cur2 = Stat.StatItems(mapOf("Lab work" to 1.0, "In class" to 7.0))
        stat.updateCurrent(mapOf("Lab work" to 1.0, "In class" to 7.0))
        assertEquals(cur2, stat.current)

        val cur3 = Stat.StatItems(mapOf("Lab work" to 5.0, "In class" to 7.0))
        stat.updateCurrent(mapOf("Lab work" to 4.0))
        assertEquals(cur3, stat.current)
    }

    @Test
    fun testCalcStat() {
        stat.updateCurrent(mapOf("Running" to 3.0, "Exercise" to 6.0))
        assertEquals(0.0, stat.calcStat() ?: 0.0, EPS)

        stat.updateCurrent(mapOf("Lab work" to 1.0, "In class" to 7.0))
        assertEquals(8.0 / 12.0, stat.calcStat() ?: 0.0, EPS)

        stat.updateCurrent(mapOf("Lab work" to 2.5, "In class" to 7.5))
        assertEquals(11.5 / 12.0, stat.calcStat() ?: 0.0, EPS)
    }

    @Test
    fun testReset() {
        stat.updateCurrent(mapOf("Running" to 3.0, "Exercise" to 6.0))
        stat.updateCurrent(mapOf("Lab work" to 4.0))
        stat.updateCurrent(mapOf("Lab work" to 1.0, "In class" to 7.0))
        stat.reset()

        val cur = Stat.StatItems(mapOf("Lab work" to 0.0, "In class" to 0.0))
        assertEquals(cur, stat.current)
    }
}
