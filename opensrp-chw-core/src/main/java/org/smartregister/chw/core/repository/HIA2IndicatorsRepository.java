package org.smartregister.chw.core.repository;

import android.database.Cursor;
import android.util.Log;

import net.sqlcipher.database.SQLiteDatabase;

import org.smartregister.chw.core.domain.Hia2Indicator;
import org.smartregister.repository.BaseRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HIA2IndicatorsRepository extends BaseRepository {
    private static final String TAG = HIA2IndicatorsRepository.class.getCanonicalName();
    private static final String HIA2_INDICATORS_TABLE_NAME = "indicators";
    private static final String ID_COLUMN = "_id";
    private static final String INDICATOR_CODE = "indicator_code";
    private static final String DESCRIPTION = "description";
    private static final String GROUPING = "grouping";

    private static final String[] HIA2_TABLE_COLUMNS = {HIA2_INDICATORS_TABLE_NAME + "." + ID_COLUMN, HIA2_INDICATORS_TABLE_NAME + "." + INDICATOR_CODE, HIA2_INDICATORS_TABLE_NAME + "." + DESCRIPTION, GROUPING};

    public HashMap<String, Hia2Indicator> findAll() {
        HashMap<String, Hia2Indicator> response = new HashMap<>();
        Cursor cursor = null;

        try {
            String table = HIA2_INDICATORS_TABLE_NAME + " INNER JOIN indicator_queries ON " + HIA2_INDICATORS_TABLE_NAME + ".indicator_code = indicator_queries.indicator_code";
            cursor = getReadableDatabase().query(table, HIA2_TABLE_COLUMNS, null, null, null, null, null, null);
            List<Hia2Indicator> hia2Indicators = readAllDataElements(cursor);
            for (Hia2Indicator curIndicator : hia2Indicators) {
                response.put(curIndicator.getIndicatorCode(), curIndicator);
            }
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return response;
    }

    private List<Hia2Indicator> readAllDataElements(Cursor cursor) {
        List<Hia2Indicator> hia2Indicators = new ArrayList<>();
        try {
            if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    Hia2Indicator hia2Indicator = new Hia2Indicator();
                    hia2Indicator.setId(cursor.getLong(cursor.getColumnIndex(ID_COLUMN)));
                    hia2Indicator.setDescription(cursor.getString(cursor.getColumnIndex(DESCRIPTION)));
                    hia2Indicator.setIndicatorCode(cursor.getString(cursor.getColumnIndex(INDICATOR_CODE)));

                    if (cursor.getString(cursor.getColumnIndex(GROUPING)) != null)
                        hia2Indicator.setCategory(cursor.getString(cursor.getColumnIndex(GROUPING)));
                    hia2Indicators.add(hia2Indicator);

                    cursor.moveToNext();
                }
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return hia2Indicators;

    }

    /**
     * order by id asc so that the indicators are ordered by category and indicator id
     *
     * @return
     */
    public List<Hia2Indicator> fetchAll() {
        SQLiteDatabase database = getReadableDatabase();
        String table = HIA2_INDICATORS_TABLE_NAME + " INNER JOIN indicator_queries ON " + HIA2_INDICATORS_TABLE_NAME + ".indicator_code = indicator_queries.indicator_code";
        String orderBy = HIA2_INDICATORS_TABLE_NAME + "." + ID_COLUMN + " asc";
        Cursor cursor = database.query(table, HIA2_TABLE_COLUMNS, null, null, null, null, orderBy);
        return readAllDataElements(cursor);
    }
}

