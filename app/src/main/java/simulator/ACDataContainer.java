package simulator;

/**
 * Created by AlessandroSam on 06.08.2015.
 */

import android.util.Log;

import org.json.JSONException; // ?!
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class ACDataContainer {
    // Field definitions

    public static final String FSTR_TYPE = "type";
    public static final String MTYPE_EMPTY = "empty";
    public static final String MTYPE_STATIC = "static";
    public static final String MTYPE_DYNAMIC = "dynamic";
    public static final String MTYPE_TIMING = "timing";

    // Physics fields (dynamic)
    public static final String P_packetId = "packetId";
    public static final String P_gas = "gas";
    public static final String P_brake = "brake";
    public static final String P_fuel = "fuel";
    public static final String P_gear = "gear";
    public static final String P_rpms = "rpms";
    public static final String P_steerAngle = "steerAngle";
    public static final String P_speedKmh = "speedKmh";
    public static final String P_velocity = "velocity";
    public static final String P_accG = "accG";
    public static final String P_wheelSlip = "wheelSlip";
    public static final String P_wheelLoad = "wheelLoad";
    public static final String P_wheelsPressure = "wheelsPressure";
    public static final String P_wheelAngularSpeed = "wheelAngularSpeed";
    public static final String P_tyreWear = "tyreWear";
    public static final String P_tyreDirtyLevel = "tyreDirtyLevel";
    public static final String P_tyreCoreTemperature = "tyreCoreTemperature";
    public static final String P_camberRAD = "camberRAD";
    public static final String P_suspensionTravel = "suspensionTravel";
    public static final String P_drs = "drs";
    public static final String P_tc = "tc";
    public static final String P_heading = "heading";
    public static final String P_pitch = "pitch";
    public static final String P_roll = "roll";
    public static final String P_cgHeight = "cgHeight";
    public static final String P_carDamage = "carDamage";
    public static final String P_numberOfTyresOut = "numberOfTyresOut";
    public static final String P_pitLimiterOn = "pitLimiterOn";
    public static final String P_abs = "abs";

    // Graphics fields (dynamic)
    public static final String G_packetId = "packetId";
    public static final String G_status = "status";
    public static final String G_session = "session";
    public static final String G_currentTime = "currentTime";
    public static final String G_lastTime = "lastTime";
    public static final String G_bestTime = "bestTime";
    public static final String G_split = "split";
    public static final String G_completedLaps = "completedLaps";
    public static final String G_position = "position";
    public static final String G_iCurrentTime = "iCurrentTime";
    public static final String G_iBestTime = "iBestTime";
    public static final String G_sessionTimeLeft = "sessionTimeLeft";
    public static final String G_distanceTraveled = "distanceTraveled";
    public static final String G_isInPit = "isInPit";
    public static final String G_currentSectorIndex = "currentSectorIndex";
    public static final String G_lastSectorTime = "lastSectorTime";
    public static final String G_numberOfLaps = "numberOfLaps";
    public static final String G_tyreCompound = "tyreCompound";
    public static final String G_replayTimeMultiplier = "replayTimeMultiplier";
    public static final String G_normalizedCarPosition = "normalizedCarPosition";
    public static final String G_carCoordinates = "carCoordinates";

    // Static fields - do not change during single session
    // An 'empty' message is received between sessions
    public static final String S_smVersion = "_smVersion";
    public static final String S_acVersion = "_acVersion";
    public static final String S_numberOfSessions = "numberOfSessions";
    public static final String S_numCars = "numCars";
    public static final String S_carModel = "carModel";
    public static final String S_track = "track";
    public static final String S_playerName = "playerName";
    public static final String S_playerSurname = "playerSurname";
    public static final String S_playerNick = "playerNick";
    public static final String S_sectorCount = "sectorCount";
    public static final String S_maxTorque = "maxTorque";
    public static final String S_maxPower = "maxPower";
    public static final String S_maxRpm = "maxRpm";
    public static final String S_maxFuel = "maxFuel";
    public static final String S_suspensionMaxTravel = "suspensionMaxTravel";
    public static final String S_tyreRadius = "tyreRadius";

    /* Status constants */
    public static final String ST_AC_OFF = "0";
    public static final String ST_AC_REPLAY = "1";
    public static final String ST_AC_LIVE = "2";
    public static final String ST_AC_PAUSE = "3";

    /* Session constants */
    public static final String SESS_AC_UNKNOWN = "-1";
    public static final String SESS_AC_PRACTICE = "0";
    public static final String SESS_AC_QUALIFY = "1";
    public static final String SESS_AC_RACE = "2";
    public static final String SESS_AC_HOTLAP = "3";
    public static final String SESS_AC_TIME_ATTACK = "4";
    public static final String SESS_AC_DRIFT = "5";
    public static final String SESS_AC_DRAG = "6";

    /* Error tags */
    public final String ERRTAG_PARSER = "JSONParser";
    public static final String ERRTAG_DATA = "Data retrieval";

    private JSONObject object = null;
    private JSONParser parser;

    public ACDataContainer(String json) throws ParseException {
        parser = new JSONParser();
        object = (JSONObject) parser.parse(json);
    }

    // It's class user's responsibility to choose correct method :(
    // No way to know if the field is float, int, string or array
    // Use getStringField() for data display purposes, even if the field isn't a string

    public String getDataType() {
        Object fld = object.get(FSTR_TYPE);
        String result = fld.toString();
        return result;
    }

    public String getStringField(String field)  {
        Object fld = object.get(field);
        if (fld == null)
            return "";
        else return fld.toString();
    }

    public int getIntegerField(String field) {
        Integer fld = (Integer) object.get(field);
        return fld.intValue();
    }

    public float getFloatField(String field) {
        Float fld = (Float) object.get(field);
        return fld.floatValue();
    }

    public float[] getTyreData(String field) {
        // Every array in received JSON is tyre data: load, slip, ...
        // It always contains floats
        // Useful for some little computation (still inaccurate due to rounded values in JSON)
        JSONArray arr = (JSONArray) object.get(field);
        float[] ret = new float[4];
        for (int i = 0; i < 4; i++) {
            ret[i] = ((float) arr.get(i));
        }
        return ret;
    }

    public String[] getTyreDataAsString(String field) {
        // Same as above, return String array.
        // Can have better performance since it is received as String so no need to parse floats
        JSONArray arr = (JSONArray) object.get(field);
        String[] ret = new String[4];
        for (int i = 0; i < 4; i++) {
            ret[i] = ((String) arr.get(i));
        }
        return ret;
    }
}
