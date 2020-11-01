package evo_sim.model

/** Constants representing the simulation parameters. */
object Constants {

  /** The simulation world height in coordinates (x axis) */
  val WORLD_WIDTH = 1280
  /** The simulation world width in coordinates (y axis) */
  val WORLD_HEIGHT = 720

  /** The simulation loop period in milliseconds */
  //val SIMULATION_LOOP_PERIOD = 10 TODO

  /** How many simulation loops in a day */
  val ITERATIONS_PER_DAY = 50
  /** Time lapse between one interaction and the next */
  //val ITERATION_LAPSE: Double = 0.017
  val ITERATION_LAPSE: Double = 0.05

  /** Growth rate of the temperature */
  val TEMPERATURE_AMPLITUDE = 1.0125f
  /** Growth rate of the luminosity */
  val LUMINOSITY_AMPLITUDE = 1.05f
  /** Growth rate of a blob's field of view radius */

  /** A blob's default size radius */
  val DEF_BLOB_RADIUS = 5
  /** Minimum value of blobs radius */
  val MIN_BLOB_RADIUS = 5
  /** A blob's default life */
  val DEF_BLOB_LIFE = 1250
  /** A blob's default velocity */
  val DEF_BLOB_VELOCITY = 50
  /** A blob's default velocity when in slow status */
  val DEF_BLOB_SLOW_VELOCITY: Int = DEF_BLOB_VELOCITY / 2
  /** A blob's minimum velocity */
  val MIN_BLOB_VELOCITY: Int = DEF_BLOB_SLOW_VELOCITY
  /** A blob's default field of view radius */
  val DEF_BLOB_FOW_RADIUS = 50
  /** Minimum value of  field of view radius */
  val MIN_BLOB_FOW_RADIUS = 15
  /** A blob's default movement direction */
  val DEF_NEXT_DIRECTION = 0
  /** Maximum number of steps a blob can take in one direction */
  val MAX_STEP_FOR_ONE_DIRECTION = 50
  /** A blob's life at death */
  val DEF_BLOB_DEAD = 0

  /** A food's default height */
  val DEF_FOOD_HEIGHT = 10
  /** A reproduction food's default height */
  val DEF_REPRODUCING_FOOD_HEIGHT = 15
  /** A poisonous food's default height */
  val DEF_POISONOUS_FOOD_HEIGHT = 12
  /** A food's default life */
  val DEF_FOOD_LIFE = 500

  /** A puddle's default width */
  val DEF_PUDDLE_WIDTH = 50
  /** A puddle's default height */
  val DEF_PUDDLE_HEIGHT = 35

  /** A stone's default width */
  val DEF_STONE_WIDTH = 15
  /** A stone's default height */
  val DEF_STONE_HEIGHT = 12

  /** A plant's default width */
  val DEF_STANDARD_PLANT_WIDTH = 7
  /** A plant's default height */
  val DEF_STANDARD_PLANT_HEIGHT = 10
  /** A reproduction plant's default width */
  val DEF_REPRODUCING_PLANT_WIDTH = 10
  /** A reproduction plant's default height */
  val DEF_REPRODUCING_PLANT_HEIGHT = 10
  /** A poisonous plant's default width */
  val DEF_POISONOUS_PLANT_WIDTH = 8
  /** A poisonous plant's default height */
  val DEF_POISONOUS_PLANT_HEIGHT = 10

  /** An effect's default cooldown counter */
  val DEF_COOLDOWN = 50
  /** Damage effect's life subtraction */
  val DEF_DAMAGE = 250
  /** A food's life addition */
  val DEF_FOOD_ENERGY = 500

  /** Minimum amount of blobs allowed in a simulation */
  val MIN_BLOBS = 0
  /** Maximum amount of blobs allowed in a simulation */
  val MAX_BLOBS = 100
  /** Default amount of blobs in a simulation */
  val DEF_BLOBS = 50

  /** Minimum amount of plants allowed in a simulation */
  val MIN_PLANTS = 0
  /** Maximum amount of plants allowed in a simulation */
  val MAX_PLANTS = 50
  /** Default amount of plants in a simulation */
  val DEF_PLANTS = 25

  /** Minimum amount of obstacles allowed in a simulation */
  val MIN_OBSTACLES = 0
  /** Maximum amount of obstacles allowed in a simulation */
  val MAX_OBSTACLES = 100
  /** Default amount of obstacles in a simulation */
  val DEF_OBSTACLES = 20

  /** Default iterations required to make a plant generate food */
  val DEF_LIFECYCLE = 250

  /** Minimum luminosity allowed in a simulation */
  val MIN_LUMINOSITY = 0
  /** Maximum luminosity allowed in a simulation */
  val MAX_LUMINOSITY = 200
  /** Default luminosity in a simulation */
  val DEF_LUMINOSITY = 100

  /** Minimum temperature allowed in a simulation */
  val MIN_TEMPERATURE: Int = -20
  /** Maximum temperature allowed in a simulation */
  val MAX_TEMPERATURE = 75
  /** Default temperature in a simulation */
  val DEF_TEMPERATURE = 25
  /** Temperature offset to have positive values */
  val TEMPERATURE_OFFSET: Int = -MIN_TEMPERATURE

  /** Minimum days a simulation can last */
  val MIN_DAYS = 1
  /** Maximum days a simulation can last */
  val MAX_DAYS = 366
  /** Default days a simulation lasts */
  val DEF_DAYS = 30

  /** How much the temperature affects the velocity of a blob */
  val VELOCITY_MODIFIER = 0.0253125f
  /** How much the luminosity affects the field of view radius of a blob */
  val FOW_MODIFIER = 0.01265625f

  /** How much the temperature ranges in a simulation */
  val TEMPERATURE_MAX_DELTA = 25
  /** How much the luminosity ranges in a simulation */
  val LUMINOSITY_MAX_DELTA = 50

  /** Maximum luminosity selectable in a simulation */
  val SELECTABLE_MAX_LUMINOSITY: Int = MAX_LUMINOSITY - LUMINOSITY_MAX_DELTA
  /** Minimum luminosity selectable in a simulation */
  val SELECTABLE_MIN_LUMINOSITY: Int = MIN_LUMINOSITY

  /** Maximum temperature selectable in a simulation */
  val SELECTABLE_MAX_TEMPERATURE: Int = MAX_TEMPERATURE - TEMPERATURE_MAX_DELTA
  /** Minimum temperature selectable in a simulation */
  val SELECTABLE_MIN_TEMPERATURE: Int = MIN_TEMPERATURE

  /** Slow blob type string */
  val SLOWBLOB_TYPE = "SlowBlob"
  /** Poison blob type string */
  val POISONBLOB_TYPE = "PoisonBlob"

  /** Range within update a parameter based on an initial value */
  val DEF_MOD_PROP_RANGE = 5

  /** Equilateral triangle angle */
  val DEF_EQUILATERAL_ANGLE = 60.0

  /** Standard life degradation */
  val STANDARD_LIFE_DECREASE = 1
}
