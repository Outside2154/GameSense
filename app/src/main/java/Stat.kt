import java.util.Map;

/**
 * Created by iammichelleau on 11/1/17.
 */

class Stat(initGoals: HashMap<String, Double>) {
    private var goals = hashMapOf("Health" to 0.0, "Int" to 0.0, "Atk" to 0.0)
    private var current = hashMapOf("Health" to 0, "Int" to 0, "Atk" to 0)
    private var stat: Double = 0 as Double

    init {
        this.goals = initGoals
    }



}
