package edu.outside2154.gamesense;

public class Boss {
    private val BASE_HEALTH: Double = 100.0
    private val BASE_HEALTH_INC: Double = 50.0
    private val BASE_ATK: Double = 20.0
    private val BASE_ATK_INC: Double = 5.0

    private var health: Double
    private var atk: Double
    private var level: Int
    private var avatar: String

    init {
        this.health = 100.0
        this.atk = 20.0
        this.level = 1
        this.avatar = ""
    }

    public fun takeDamage(damage: Double) {
        this.health -= maxOf(this.health - damage, 0.0)
    }

    public fun isDead(): Boolean {
        return this.health.equals(0.0)
    }

    public fun reset(userWon: Boolean) {
        if (userWon) {
            this.level++
        }
        this.health = BASE_HEALTH + BASE_HEALTH_INC * (this.level - 1)
        this.atk = this.BASE_ATK + BASE_ATK_INC * (this.level - 1)
    }

    public fun getAtk(): Double {
        return this.atk
    }
}
