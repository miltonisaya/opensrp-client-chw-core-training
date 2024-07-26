package org.smartregister.chw.core.interactor;

import static org.smartregister.chw.core.utils.CoreConstants.TABLE_NAME.MOTHER_CHAMPION;
import static org.smartregister.chw.core.utils.QueryConstant.ANC_DANGER_SIGNS_OUTCOME_COUNT_QUERY;
import static org.smartregister.chw.core.utils.QueryConstant.FAMILY_PLANNING_UPDATE_COUNT_QUERY;
import static org.smartregister.chw.core.utils.QueryConstant.HIV_INDEX_CONTACT_COMMUNITY_FOLLOWUP_REFERRAL_COUNT_QUERY;
import static org.smartregister.chw.core.utils.QueryConstant.HIV_OUTCOME_COUNT_QUERY;
import static org.smartregister.chw.core.utils.QueryConstant.MALARIA_HF_FOLLOW_UP_COUNT_QUERY;
import static org.smartregister.chw.core.utils.QueryConstant.NOT_YET_DONE_REFERRAL_COUNT_QUERY;
import static org.smartregister.chw.core.utils.QueryConstant.PNC_DANGER_SIGNS_OUTCOME_COUNT_QUERY;
import static org.smartregister.chw.core.utils.QueryConstant.PREGNANCY_CONFIRMATION_UPDATES_COUNT_QUERY;
import static org.smartregister.chw.core.utils.QueryConstant.SICK_CHILD_FOLLOW_UP_COUNT_QUERY;
import static org.smartregister.chw.core.utils.QueryConstant.TB_OUTCOME_COUNT_QUERY;
import static org.smartregister.util.Utils.getAllSharedPreferences;

import org.smartregister.chw.cdp.util.DBConstants;
import org.smartregister.chw.core.contract.CoreApplication;
import org.smartregister.chw.core.contract.NavigationContract;
import org.smartregister.chw.core.custom_views.NavigationMenu;
import org.smartregister.chw.core.dao.NavigationDao;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.fp.util.FamilyPlanningConstants;
import org.smartregister.chw.referral.util.Constants;
import org.smartregister.family.util.AppExecutors;

import java.util.Date;

import timber.log.Timber;

public class NavigationInteractor implements NavigationContract.Interactor {

    protected static NavigationInteractor instance;
    protected AppExecutors appExecutors = new AppExecutors();
    private CoreApplication coreApplication;

    protected NavigationInteractor() {

    }

    public static NavigationInteractor getInstance() {
        if (instance == null) {
            instance = new NavigationInteractor();
        }

        return instance;
    }

    @Override
    public Date getLastSync() {
        return null;
    }

    @Override
    public void getRegisterCount(final String tableName,
                                 final NavigationContract.InteractorCallback<Integer> callback) {
        if (callback != null) {
            appExecutors.diskIO().execute(() -> {
                try {
                    final Integer finalCount = getCount(tableName);
                    appExecutors.mainThread().execute(() -> callback.onResult(finalCount));
                } catch (final Exception e) {
                    appExecutors.mainThread().execute(() -> callback.onError(e));
                }
            });

        }
    }

    @Override
    public Date sync() {
        Date res = null;
        try {
            res = new Date(getLastCheckTimeStamp());
        } catch (Exception e) {
            Timber.e(e.toString());
        }
        return res;
    }

    @Override
    public void setApplication(CoreApplication coreApplication) {
        this.coreApplication = coreApplication;
    }

    private String getChildSqlString() {
        if (NavigationMenu.getChildNavigationCountString() == null) {
            return "select count(*) from ec_child c " +
                    "inner join ec_family_member m on c.base_entity_id = m.base_entity_id COLLATE NOCASE " +
                    "inner join ec_family f on f.base_entity_id = m.relational_id COLLATE NOCASE " +
                    "where m.date_removed is null and m.is_closed = 0 " +
                    "and ((( julianday('now') - julianday(c.dob))/365.25) < 5) and c.is_closed = 0 " +
                    " and (( ( ifnull(entry_point,'') <> 'PNC' ) ) or (ifnull(entry_point,'') = 'PNC' and ( date (c.dob, '+42 days') <= date() and ((SELECT is_closed FROM ec_family_member WHERE base_entity_id = mother_entity_id ) = 0)))  or (ifnull(entry_point,'') = 'PNC'  and (SELECT is_closed FROM ec_family_member WHERE base_entity_id = mother_entity_id ) = 1)) ";
        } else {
            return NavigationMenu.getChildNavigationCountString();
        }
    }

    protected int getCount(String tableName) {
        switch (tableName.toLowerCase().trim()) {
            case CoreConstants.TABLE_NAME.CHILD:
                String sqlChild = getChildSqlString();
                return NavigationDao.getQueryCount(sqlChild);

            case CoreConstants.TABLE_NAME.FAMILY:
                String sqlFamily = "select count(*) from ec_family where date_removed is null AND (entity_type = 'ec_family' OR entity_type = 'ec_family_member' OR entity_type IS NULL)";
                return NavigationDao.getQueryCount(sqlFamily);

            case CoreConstants.TABLE_NAME.ANC_MEMBER:
                String sqlAncMember = "select count(*) " +
                        "from ec_anc_register r " +
                        "inner join ec_family_member m on r.base_entity_id = m.base_entity_id COLLATE NOCASE " +
                        "inner join ec_family f on f.base_entity_id = m.relational_id COLLATE NOCASE " +
                        "where m.date_removed is null and m.is_closed = 0 and r.is_closed = 0 ";
                return NavigationDao.getQueryCount(sqlAncMember);

            case CoreConstants.TABLE_NAME.TASK:
                String sqlTask = String.format("select count(*) from task inner join " +
                        "ec_family_member member on member.base_entity_id = task.for COLLATE NOCASE " +
                        "WHERE task.business_status = '%s' and member.date_removed is null ", CoreConstants.BUSINESS_STATUS.REFERRED);
                return NavigationDao.getQueryCount(sqlTask);

            case CoreConstants.TABLE_NAME.ANC_PREGNANCY_OUTCOME:
                String sqlPregnancy = "select count(*) " +
                        "from ec_pregnancy_outcome p " +
                        "inner join ec_family_member m on p.base_entity_id = m.base_entity_id COLLATE NOCASE " +
                        "inner join ec_family f on f.base_entity_id = m.relational_id COLLATE NOCASE " +
                        "where m.date_removed is null and p.delivery_date is not null and p.is_closed = 0 and m.is_closed = 0 ";
                return NavigationDao.getQueryCount(sqlPregnancy);

            case CoreConstants.TABLE_NAME.MALARIA_CONFIRMATION:
                String sqlMalaria = "select count (p.base_entity_id) from ec_malaria_confirmation p " +
                        "inner join ec_family_member m on p.base_entity_id = m.base_entity_id COLLATE NOCASE " +
                        "inner join ec_family f on f.base_entity_id = m.relational_id COLLATE NOCASE " +
                        "where m.date_removed is null and p.is_closed = 0 " +
                        "AND datetime('NOW') <= datetime(p.last_interacted_with/1000, 'unixepoch', 'localtime','+15 days')";
                return NavigationDao.getQueryCount(sqlMalaria);

            case CoreConstants.TABLE_NAME.ICCM_ENROLLMENT:
                String sqlIccm = "select count (p.entity_id) from ec_iccm_enrollment p " +
                        "inner join ec_family_member m on p.entity_id = m.base_entity_id COLLATE NOCASE " +
                        "where m.date_removed is null and p.is_closed = 0 " +
                        " AND date('now') <= date(strftime('%Y-%m-%d', p.last_interacted_with / 1000, 'unixepoch', 'localtime'))";
                return NavigationDao.getQueryCount(sqlIccm);

            case FamilyPlanningConstants.TABLES.FP_REGISTER:
                String sqlFP = "select count(*) " +
                        "from " + FamilyPlanningConstants.TABLES.FP_REGISTER + " p " +
                        "inner join ec_family_member m on p.base_entity_id = m.base_entity_id COLLATE NOCASE " +
                        "inner join ec_family f on f.base_entity_id = m.relational_id COLLATE NOCASE " +
                        "where m.date_removed is null and p.is_closed = 0 ";
                return NavigationDao.getQueryCount(sqlFP);

            case CoreConstants.TABLE_NAME.FAMILY_MEMBER:
                String allClients = "/**COUNT REGISTERED CHILD CLIENTS*/\n" +
                        "SELECT SUM(c)\n" +
                        "FROM (\n" +
                        "         SELECT COUNT(*) AS c\n" +
                        "         FROM ec_child\n" +
                        "                  inner join ec_family_member on ec_family_member.base_entity_id = ec_child.base_entity_id\n" +
                        "                  inner join ec_family on ec_family.base_entity_id = ec_family_member.relational_id\n" +
                        "         WHERE ec_family_member.is_closed = '0'\n" +
                        "           AND ec_family_member.date_removed is null\n" +
                        "           AND cast(strftime('%Y-%m-%d %H:%M:%S', 'now') - strftime('%Y-%m-%d %H:%M:%S', ec_child.dob) as int) > 0\n" +
                        "         UNION ALL\n" +
                        "/**COUNT REGISTERED ANC CLIENTS*/\n" +
                        "         SELECT COUNT(*) AS c\n" +
                        "         FROM ec_anc_register\n" +
                        "                  inner join ec_family_member on ec_family_member.base_entity_id = ec_anc_register.base_entity_id\n" +
                        "                  inner join ec_family on ec_family.base_entity_id = ec_family_member.relational_id\n" +
                        "         where ec_family_member.date_removed is null\n" +
                        "           and ec_anc_register.is_closed is 0\n" +
                        "         UNION ALL\n" +
                        "/**COUNT REGISTERED PNC CLIENTS*/\n" +
                        "         SELECT COUNT(*) AS c\n" +
                        "         FROM ec_pregnancy_outcome\n" +
                        "                  inner join ec_family_member on ec_family_member.base_entity_id = ec_pregnancy_outcome.base_entity_id\n" +
                        "                  inner join ec_family on ec_family.base_entity_id = ec_family_member.relational_id\n" +
                        "         where ec_family_member.date_removed is null\n" +
                        "           and ec_pregnancy_outcome.is_closed is 0\n" +
                        "           AND ec_pregnancy_outcome.base_entity_id NOT IN\n" +
                        "               (SELECT base_entity_id FROM ec_anc_register WHERE ec_anc_register.is_closed IS 0)\n" +
                        "         UNION ALL\n" +
                        "/*COUNT OTHER FAMILY MEMBERS*/\n" +
                        "         SELECT COUNT(*) AS c\n" +
                        "         FROM ec_family_member\n" +
                        "                  inner join ec_family on ec_family.base_entity_id = ec_family_member.relational_id\n" +
                        "         where ec_family_member.date_removed is null\n" +
                        "           AND (ec_family.entity_type = 'ec_family' OR ec_family.entity_type is null)\n" +
                        "           AND ec_family_member.base_entity_id NOT IN (\n" +
                        "             SELECT ec_anc_register.base_entity_id AS base_entity_id\n" +
                        "             FROM ec_anc_register WHERE ec_anc_register.is_closed IS 0\n" +
                        "             UNION ALL\n" +
                        "             SELECT ec_pregnancy_outcome.base_entity_id AS base_entity_id\n" +
                        "             FROM ec_pregnancy_outcome\n" +
                        "             UNION ALL\n" +
                        "             SELECT ec_child.base_entity_id AS base_entity_id\n" +
                        "             FROM ec_child\n" +
                        "             UNION ALL\n" +
                        "             SELECT ec_malaria_confirmation.base_entity_id AS base_entity_id\n" +
                        "             FROM ec_malaria_confirmation\n" +
                        "             UNION ALL\n" +
                        "             SELECT ec_family_planning.base_entity_id AS base_entity_id\n" +
                        "             FROM ec_family_planning\n" +
                        "             UNION ALL\n" +
                        "             SELECT ec_sbc_register.base_entity_id AS base_entity_id\n" +
                        "             FROM ec_sbc_register\n" +
                        "             UNION ALL\n" +
                        "             SELECT ec_tb_register.base_entity_id AS base_entity_id\n" +
                        "             FROM ec_tb_register\n" +
                        "             WHERE ec_tb_register.tb_case_closure_date is null\n" +
                        "             UNION ALL\n" +
                        "             SELECT ec_cbhs_register.base_entity_id AS base_entity_id\n" +
                        "             FROM ec_cbhs_register WHERE is_closed is 0\n" +
                        "         )\n" +
                        "         UNION ALL\n" +
                        "/*COUNT INDEPENDENT MEMBERS*/\n" +
                        "         SELECT COUNT(*) AS c\n" +
                        "         FROM ec_family_member\n" +
                        "                  inner join ec_family on ec_family.base_entity_id = ec_family_member.relational_id\n" +
                        "         where ec_family_member.date_removed is null\n" +
                        "           AND ec_family.entity_type = 'ec_independent_client'\n" +
                        "           AND ec_family_member.base_entity_id NOT IN (\n" +
                        "             SELECT ec_anc_register.base_entity_id AS base_entity_id\n" +
                        "             FROM ec_anc_register WHERE ec_anc_register.is_closed IS 0\n" +
                        "             UNION ALL\n" +
                        "             SELECT ec_pregnancy_outcome.base_entity_id AS base_entity_id\n" +
                        "             FROM ec_pregnancy_outcome\n" +
                        "             UNION ALL\n" +
                        "             SELECT ec_child.base_entity_id AS base_entity_id\n" +
                        "             FROM ec_child\n" +
                        "             UNION ALL\n" +
                        "             SELECT ec_malaria_confirmation.base_entity_id AS base_entity_id\n" +
                        "             FROM ec_malaria_confirmation\n" +
                        "             UNION ALL\n" +
                        "             SELECT ec_sbc_register.base_entity_id AS base_entity_id\n" +
                        "             FROM ec_sbc_register\n" +
                        "             UNION ALL\n" +
                        "             SELECT ec_family_planning.base_entity_id AS base_entity_id\n" +
                        "             FROM ec_family_planning\n" +
                        "             UNION ALL\n" +
                        "             SELECT ec_tb_register.base_entity_id AS base_entity_id\n" +
                        "             FROM ec_tb_register\n" +
                        "             WHERE ec_tb_register.tb_case_closure_date is null\n" +
                        "             UNION ALL\n" +
                        "             SELECT ec_cbhs_register.base_entity_id AS base_entity_id\n" +
                        "             FROM ec_cbhs_register WHERE is_closed is 0\n" +
                        "         )\n" +
                        "         UNION ALL\n" +
                        "/**COUNT REGISTERED MALARIA CLIENTS*/\n" +
                        "         SELECT COUNT(*) AS c\n" +
                        "         FROM ec_family_member\n" +
                        "                  inner join ec_family on ec_family.base_entity_id = ec_family_member.relational_id\n" +
                        "                  inner join ec_malaria_confirmation\n" +
                        "                             on ec_family_member.base_entity_id = ec_malaria_confirmation.base_entity_id\n" +
                        "         where ec_family_member.date_removed is null\n" +
                        "           AND ec_family_member.base_entity_id NOT IN (\n" +
                        "             SELECT ec_anc_register.base_entity_id AS base_entity_id\n" +
                        "             FROM ec_anc_register\n" +
                        "             UNION ALL\n" +
                        "             SELECT ec_pregnancy_outcome.base_entity_id AS base_entity_id\n" +
                        "             FROM ec_pregnancy_outcome\n" +
                        "             UNION ALL\n" +
                        "             SELECT ec_child.base_entity_id AS base_entity_id\n" +
                        "             FROM ec_child\n" +
                        "             UNION ALL\n" +
                        "             SELECT ec_family_planning.base_entity_id AS base_entity_id\n" +
                        "             FROM ec_family_planning\n" +
                        "         )\n" +
                        "         UNION ALL\n" +
                        "/**COUNT REGISTERED TB CLIENTS*/\n" +
                        "         SELECT COUNT(*) AS c\n" +
                        "         FROM ec_family_member\n" +
                        "                  inner join ec_family on ec_family.base_entity_id = ec_family_member.relational_id\n" +
                        "                  inner join ec_tb_register\n" +
                        "                             on ec_family_member.base_entity_id = ec_tb_register.base_entity_id\n" +
                        "         where ec_family_member.date_removed is null\n" +
                        "           AND ec_family_member.base_entity_id NOT IN (\n" +
                        "             SELECT ec_anc_register.base_entity_id AS base_entity_id\n" +
                        "             FROM ec_anc_register\n" +
                        "             UNION ALL\n" +
                        "             SELECT ec_pregnancy_outcome.base_entity_id AS base_entity_id\n" +
                        "             FROM ec_pregnancy_outcome\n" +
                        "             UNION ALL\n" +
                        "             SELECT ec_child.base_entity_id AS base_entity_id\n" +
                        "             FROM ec_child\n" +
                        "             UNION ALL\n" +
                        "             SELECT ec_malaria_confirmation.base_entity_id AS base_entity_id\n" +
                        "             FROM ec_malaria_confirmation\n" +
                        "             UNION ALL\n" +
                        "             SELECT ec_family_planning.base_entity_id AS base_entity_id\n" +
                        "             FROM ec_family_planning\n" +
                        "         )\n" +
                        "         UNION ALL\n" +
                        "/**COUNT REGISTERED HIV CLIENTS*/\n" +
                        "         SELECT COUNT(*) AS c\n" +
                        "         FROM ec_family_member\n" +
                        "                  inner join ec_family on ec_family.base_entity_id = ec_family_member.relational_id\n" +
                        "                  inner join ec_cbhs_register\n" +
                        "                             on ec_family_member.base_entity_id = ec_cbhs_register.base_entity_id\n" +
                        "         where ec_family_member.date_removed is null\n" +
                        "           AND ec_cbhs_register.is_closed is 0 \n" +
                        "           AND ec_family_member.base_entity_id NOT IN (\n" +
                        "             SELECT ec_anc_register.base_entity_id AS base_entity_id\n" +
                        "             FROM ec_anc_register\n" +
                        "             UNION ALL\n" +
                        "             SELECT ec_pregnancy_outcome.base_entity_id AS base_entity_id\n" +
                        "             FROM ec_pregnancy_outcome\n" +
                        "             UNION ALL\n" +
                        "             SELECT ec_child.base_entity_id AS base_entity_id\n" +
                        "             FROM ec_child\n" +
                        "             UNION ALL\n" +
                        "             SELECT ec_malaria_confirmation.base_entity_id AS base_entity_id\n" +
                        "             FROM ec_malaria_confirmation\n" +
                        "             UNION ALL\n" +
                        "             SELECT ec_tb_register.base_entity_id AS base_entity_id\n" +
                        "             FROM ec_tb_register\n" +
                        "             WHERE ec_tb_register.tb_case_closure_date is null\n" +
                        "             UNION ALL\n" +
                        "             SELECT ec_family_planning.base_entity_id AS base_entity_id\n" +
                        "             FROM ec_family_planning\n" +
                        "         )\n" +
                        "  UNION ALL\n" +
                        "\n" +
                        "/*ONLY SBC clients*/\n" +
                        " SELECT  COUNT(*) AS c\n" +
                        " FROM ec_family_member\n" +
                        "         inner join ec_family on ec_family.base_entity_id = ec_family_member.relational_id\n" +
                        "         inner join ec_sbc_register\n" +
                        "                    on ec_family_member.base_entity_id = ec_sbc_register.base_entity_id\n" +
                        " WHERE ec_family_member.date_removed is null\n" +
                        "  AND ec_sbc_register.is_closed is 0\n" +
                        "  AND ec_family_member.base_entity_id NOT IN (\n" +
                        "    SELECT ec_kvp_prep_register.base_entity_id AS base_entity_id\n" +
                        "    FROM ec_kvp_prep_register where ec_kvp_prep_register.is_closed is 0\n" +
                        "    UNION ALL\n" +
                        "    SELECT ec_anc_register.base_entity_id AS base_entity_id\n" +
                        "    FROM ec_anc_register\n" +
                        "    UNION ALL\n" +
                        "    SELECT ec_pregnancy_outcome.base_entity_id AS base_entity_id\n" +
                        "    FROM ec_pregnancy_outcome\n" +
                        "    UNION ALL\n" +
                        "    SELECT ec_child.base_entity_id AS base_entity_id\n" +
                        "    FROM ec_child\n" +
                        "    UNION ALL\n" +
                        "    SELECT ec_malaria_confirmation.base_entity_id AS base_entity_id\n" +
                        "    FROM ec_malaria_confirmation\n" +
                        "    UNION ALL\n" +
                        "    SELECT ec_tb_register.base_entity_id AS base_entity_id\n" +
                        "    FROM ec_tb_register\n" +
                        "    WHERE ec_tb_register.tb_case_closure_date is null\n" +
                        "    UNION ALL\n" +
                        "    SELECT ec_cbhs_register.base_entity_id AS base_entity_id\n" +
                        "    FROM ec_cbhs_register)\n" +
                        "         UNION ALL\n" +
                        "/**COUNT FAMILY_PLANNING CLIENTS*/\n" +
                        "         SELECT COUNT(*) AS c\n" +
                        "         FROM ec_family_member\n" +
                        "                  inner join ec_family on ec_family.base_entity_id = ec_family_member.relational_id\n" +
                        "                  inner join ec_family_planning on ec_family_member.base_entity_id = ec_family_planning.base_entity_id\n" +
                        "         where ec_family_member.date_removed is null\n" +
                        "           AND ec_family_member.base_entity_id NOT IN (\n" +
                        "             SELECT ec_anc_register.base_entity_id AS base_entity_id\n" +
                        "             FROM ec_anc_register\n" +
                        "             UNION ALL\n" +
                        "             SELECT ec_pregnancy_outcome.base_entity_id AS base_entity_id\n" +
                        "             FROM ec_pregnancy_outcome\n" +
                        "             UNION ALL\n" +
                        "             SELECT ec_child.base_entity_id AS base_entity_id\n" +
                        "             FROM ec_child\n" +
                        "             UNION ALL\n" +
                        "             SELECT ec_malaria_confirmation.base_entity_id AS base_entity_id\n" +
                        "             FROM ec_malaria_confirmation\n" +
                        "         ));";
                return NavigationDao.getQueryCount(allClients);

            case Constants.Tables.REFERRAL:
                String sqlReferral = "select count(*) " +
                        "from " + Constants.Tables.REFERRAL + " p " +
                        "inner join ec_family_member m on p.entity_id = m.base_entity_id COLLATE NOCASE " +
                        "inner join ec_family f on f.base_entity_id = m.relational_id COLLATE NOCASE " +
                        "inner join task t on p.id = t.reason_reference COLLATE NOCASE " +
                        "where m.date_removed is null and t.business_status = '" + CoreConstants.BUSINESS_STATUS.REFERRED + "' " +
                        "AND p.chw_referral_service <> 'LTFU' COLLATE NOCASE ";
                return NavigationDao.getQueryCount(sqlReferral);

            case CoreConstants.TABLE_NAME.NOTIFICATION_UPDATE:
                String referralNotificationQuery =
                        String.format("SELECT SUM(c) FROM (\n %s \nUNION ALL\n %s \nUNION ALL\n %s \nUNION ALL\n %s \nUNION ALL\n %s \nUNION ALL %s \nUNION ALL %s \nUNION ALL %s \nUNION ALL %s \nUNION ALL %s)",
                                SICK_CHILD_FOLLOW_UP_COUNT_QUERY, ANC_DANGER_SIGNS_OUTCOME_COUNT_QUERY,
                                PNC_DANGER_SIGNS_OUTCOME_COUNT_QUERY, FAMILY_PLANNING_UPDATE_COUNT_QUERY,
                                MALARIA_HF_FOLLOW_UP_COUNT_QUERY, HIV_OUTCOME_COUNT_QUERY,
                                TB_OUTCOME_COUNT_QUERY, HIV_INDEX_CONTACT_COMMUNITY_FOLLOWUP_REFERRAL_COUNT_QUERY, PREGNANCY_CONFIRMATION_UPDATES_COUNT_QUERY, NOT_YET_DONE_REFERRAL_COUNT_QUERY);
                return NavigationDao.getQueryCount(referralNotificationQuery);

            case org.smartregister.chw.hiv.util.Constants.Tables.HIV:
                String sqlHiv =
                        "SELECT SUM(c)\n" +
                                "FROM (\n" +
                                "              select count(*) as c " +
                                "              from " + org.smartregister.chw.hiv.util.Constants.Tables.HIV + " p " +
                                "              inner join ec_family_member m on p.base_entity_id = m.base_entity_id COLLATE NOCASE " +
                                "              inner join ec_family f on f.base_entity_id = m.relational_id COLLATE NOCASE " +
                                "              where m.date_removed is null and p.is_closed =  '0' and " +
                                "              UPPER (p.client_hiv_status_after_testing) LIKE UPPER('Positive') " +
                                "         UNION ALL\n" +
                                "              select count(*) as c " +
                                "              from " + org.smartregister.chw.hiv.util.Constants.Tables.HIV_COMMUNITY_FOLLOWUP + " p " +
                                "              inner join ec_family_member m on p.entity_id = m.base_entity_id COLLATE NOCASE " +
                                "              inner join ec_family f on f.base_entity_id = m.relational_id COLLATE NOCASE " +
                                "              where m.date_removed is null and p.is_closed = '0' AND " +
                                "              p.base_entity_id NOT IN (SELECT community_referral_form_id FROM " + org.smartregister.chw.hiv.util.Constants.Tables.HIV_COMMUNITY_FEEDBACK + " ))";
                return NavigationDao.getQueryCount(sqlHiv);

            case CoreConstants.TABLE_NAME.CBHS_CLIENTS:
                String sqlCbhs =
                        "SELECT SUM(c)\n" +
                                "FROM (\n" +
                                "              select count(*) as c " +
                                "              from ec_cbhs_register p " +
                                "              inner join ec_family_member m on p.base_entity_id = m.base_entity_id COLLATE NOCASE " +
                                "              inner join ec_family f on f.base_entity_id = m.relational_id COLLATE NOCASE " +
                                "              where m.date_removed is null and p.is_closed = '0')";
                return NavigationDao.getQueryCount(sqlCbhs);

            case CoreConstants.TABLE_NAME.HTS_MEMBERS:
                String sqlHts =
                        "SELECT SUM(c)\n" +
                                "FROM (\n" +
                                "              select count(*) as c " +
                                "              from " + CoreConstants.TABLE_NAME.HTS_MEMBERS + " p " +
                                "              inner join ec_family_member m on p.base_entity_id = m.base_entity_id COLLATE NOCASE " +
                                "              inner join ec_family f on f.base_entity_id = m.relational_id COLLATE NOCASE " +
                                "              where m.date_removed is null and p.is_closed = '0' and p.ctc_number is null and p.chw_referral_service = '" + CoreConstants.TASKS_FOCUS.CONVENTIONAL_HIV_TEST + "' and " +
                                "              p.client_hiv_status_after_testing IS NULL " +
                                "              and p.base_entity_id NOT IN (SELECT base_entity_id FROM " + org.smartregister.chw.hiv.util.Constants.Tables.HIV_INDEX_HF + " ))";
                return NavigationDao.getQueryCount(sqlHts);

            case org.smartregister.chw.hiv.util.Constants.Tables.HIV_INDEX:
                String sqlIndex =
                        "SELECT count(*) " +
                                "              from " + org.smartregister.chw.hiv.util.Constants.Tables.HIV_INDEX + " p " +
                                "              inner join ec_family_member m on p.base_entity_id = m.base_entity_id COLLATE NOCASE " +
                                "              inner join ec_family f on f.base_entity_id = m.relational_id COLLATE NOCASE " +
                                "              where m.date_removed is null and " +
                                "              p.test_results IS NULL and p.refer_to_chw = 'Yes' COLLATE NOCASE and p.how_to_notify_the_contact_client <> 'na' ";
                return NavigationDao.getQueryCount(sqlIndex);

            case org.smartregister.chw.hiv.util.Constants.Tables.HIV_INDEX_HF:
                String sqlIndexHf =
                        "SELECT count(*) " +
                                "              from " + org.smartregister.chw.hiv.util.Constants.Tables.HIV_INDEX_HF + " p " +
                                "              inner join ec_family_member m on p.base_entity_id = m.base_entity_id COLLATE NOCASE " +
                                "              inner join ec_family f on f.base_entity_id = m.relational_id COLLATE NOCASE " +
                                "              where m.date_removed is null and " +
                                "               p.ctc_number IS NULL AND " +
                                "               (p.test_results IS NULL OR p.test_results <> 'Negative' COLLATE NOCASE)  AND " +
                                "               p.how_to_notify_the_contact_client <> 'na' ";
                return NavigationDao.getQueryCount(sqlIndexHf);
            case org.smartregister.chw.pmtct.util.Constants.TABLES.PMTCT_REGISTRATION:
                String sqlPmtct =
                        "SELECT count(*) " +
                                "   from " + org.smartregister.chw.pmtct.util.Constants.TABLES.PMTCT_REGISTRATION + " p " +
                                "              inner join ec_family_member m on p.base_entity_id = m.base_entity_id COLLATE NOCASE " +
                                "              inner join ec_family f on f.base_entity_id = m.relational_id COLLATE NOCASE " +
                                "              where m.date_removed is null and " +
                                "              p.is_closed is 0 and " +
                                "              p.hiv_status = 'positive' ";
                return NavigationDao.getQueryCount(sqlPmtct);
            case MOTHER_CHAMPION:
                String sqlMotherChampion =
                        "SELECT count(*) " +
                                "   from " + MOTHER_CHAMPION + " p " +
                                "              inner join ec_family_member m on p.base_entity_id = m.base_entity_id COLLATE NOCASE " +
                                "              inner join ec_family f on f.base_entity_id = m.relational_id COLLATE NOCASE " +
                                "              where m.date_removed is null and p.is_closed is 0";
                return NavigationDao.getQueryCount(sqlMotherChampion);
            case org.smartregister.chw.tb.util.Constants.Tables.TB:
                String sqlTb =
                        "SELECT SUM(c)\n" +
                                "FROM (\n" +
                                "              select count(*) as c " +
                                "              from " + org.smartregister.chw.tb.util.Constants.Tables.TB + " p " +
                                "              inner join ec_family_member m on p.base_entity_id = m.base_entity_id COLLATE NOCASE " +
                                "              inner join ec_family f on f.base_entity_id = m.relational_id COLLATE NOCASE " +
                                "              where m.date_removed is null and p.is_closed = '0' and " +
                                "              p.tb_case_closure_date IS NULL and " +
                                "              (p.client_tb_status_after_testing = 'Positive' OR p.client_tb_status_after_testing IS NULL) " +
                                "         UNION ALL\n" +
                                "              select count(*) as c " +
                                "              from " + org.smartregister.chw.tb.util.Constants.Tables.TB_COMMUNITY_FOLLOWUP + " p " +
                                "              inner join ec_family_member m on p.entity_id = m.base_entity_id COLLATE NOCASE " +
                                "              inner join ec_family f on f.base_entity_id = m.relational_id COLLATE NOCASE " +
                                "              where m.date_removed is null and p.is_closed = '0' AND " +
                                "              p.base_entity_id NOT IN (SELECT community_referral_form_id FROM " + org.smartregister.chw.tb.util.Constants.Tables.TB_COMMUNITY_FEEDBACK + " ))";

                return NavigationDao.getQueryCount(sqlTb);
            case CoreConstants.TABLE_NAME.HEI:
                String sqlHei =
                        "SELECT count(*) " +
                                "   from " + CoreConstants.TABLE_NAME.HEI + " p " +
                                "              inner join ec_family_member m on p.base_entity_id = m.base_entity_id COLLATE NOCASE " +
                                "              inner join ec_family f on f.base_entity_id = m.relational_id COLLATE NOCASE " +
                                "              where m.date_removed is null and " +
                                "              p.is_closed is 0 ";
                return NavigationDao.getQueryCount(sqlHei);

            case CoreConstants.TABLE_NAME.LABOUR_AND_DELIVERY:
                String sqlLD =
                        "SELECT count(*) " +
                                "   from " + CoreConstants.TABLE_NAME.LABOUR_AND_DELIVERY + " p " +
                                "              where " +
                                "              p.labour_confirmation = 'true' AND p.is_closed is 0 ";
                return NavigationDao.getQueryCount(sqlLD);
            case CoreConstants.TABLE_NAME.LTFU_REFERRALS:
                String sqlLTFU =
                        "select count(*) " +
                                "from " + Constants.Tables.REFERRAL + " p " +
                                "inner join ec_family_member m on p.entity_id = m.base_entity_id COLLATE NOCASE " +
                                "inner join ec_family f on f.base_entity_id = m.relational_id COLLATE NOCASE " +
                                "inner join task t on p.id = t.reason_reference COLLATE NOCASE " +
                                "where m.date_removed is null and t.business_status = '" + CoreConstants.BUSINESS_STATUS.REFERRED + "' " +
                                "AND p.chw_referral_service = 'LTFU' COLLATE NOCASE ";
                return NavigationDao.getQueryCount(sqlLTFU);
            case org.smartregister.chw.agyw.util.Constants.TABLES.AGYW_REGISTER:
                String sqlAgyw =
                        "SELECT count(*) " +
                                "   from " + org.smartregister.chw.agyw.util.Constants.TABLES.AGYW_REGISTER + " p " +
                                "              where p.is_closed is 0 ";
                return NavigationDao.getQueryCount(sqlAgyw);
            case org.smartregister.chw.kvp.util.Constants.TABLES.PrEP_REGISTER:
                String sqlPrEP =
                        "SELECT count(*) " +
                                "   from " + org.smartregister.chw.kvp.util.Constants.TABLES.PrEP_REGISTER + " p " +
                                "              where p.is_closed is 0 ";
                return NavigationDao.getQueryCount(sqlPrEP);
            case org.smartregister.chw.cdp.util.Constants.TABLES.CDP_ORDERS:
                String userLocationTag = getAllSharedPreferences().fetchUserLocationTag();
                String mainOrdersTable = org.smartregister.chw.cdp.util.Constants.TABLES.CDP_ORDERS;
                String mainCondition = mainOrdersTable + "." + DBConstants.KEY.IS_CLOSED + " IS 0";
                String sqlCDPOrdersCondition;
                if (userLocationTag.contains("msd_code")) {
                    sqlCDPOrdersCondition = mainCondition + " AND (" + mainOrdersTable + "." + DBConstants.KEY.REQUEST_TYPE + " = '" + org.smartregister.chw.cdp.util.Constants.ORDER_TYPES.FACILITY_TO_FACILITY_ORDER + "'" +
                            " OR " + mainOrdersTable + "." + DBConstants.KEY.REQUEST_TYPE + " = '" + org.smartregister.chw.cdp.util.Constants.ORDER_TYPES.COMMUNITY_TO_FACILITY_ORDER + "') ";
                } else {
                    sqlCDPOrdersCondition = mainCondition + " AND " + mainOrdersTable + "." + DBConstants.KEY.REQUEST_TYPE + " = '" + org.smartregister.chw.cdp.util.Constants.ORDER_TYPES.COMMUNITY_TO_FACILITY_ORDER + "'";
                }

                String sqlCDPOrders =
                        "select count(*) " +
                                "from " + mainOrdersTable +
                                " INNER JOIN task ON  " + mainOrdersTable + "." + "base_entity_id" + " = " + "task" + "." + "for" + " COLLATE NOCASE " +
                                " where " + sqlCDPOrdersCondition;
                return NavigationDao.getQueryCount(sqlCDPOrders);
            case org.smartregister.chw.vmmc.util.Constants.TABLES.VMMC_ENROLLMENT:
                String sqlVmmc =
                        "SELECT count(*) " +
                                "   from " + org.smartregister.chw.vmmc.util.Constants.TABLES.VMMC_ENROLLMENT + " v " +
                                "              where v.is_closed is 0 ";
                return NavigationDao.getQueryCount(sqlVmmc);
            case org.smartregister.chw.sbc.util.Constants.TABLES.SBC_REGISTER:
                String sqlSbc =
                        "SELECT count(*) " +
                                "   from " + org.smartregister.chw.sbc.util.Constants.TABLES.SBC_REGISTER + " p " +
                                "              where p.is_closed is 0 ";
                return NavigationDao.getQueryCount(sqlSbc);
            case org.smartregister.chw.sbc.util.Constants.TABLES.SBC_MONTHLY_SOCIAL_MEDIA_REPORT:
                String sqlMonthlyReportsSbc =
                        "SELECT count(*) " +
                                "   from " + org.smartregister.chw.sbc.util.Constants.TABLES.SBC_MONTHLY_SOCIAL_MEDIA_REPORT + " p " +
                                "              where p.is_closed is 0 ";
                return NavigationDao.getQueryCount(sqlMonthlyReportsSbc);
            case org.smartregister.chw.gbv.util.Constants.TABLES.GBV_REGISTER:
                String sqlGbv =
                        "SELECT count(*) " +
                                "   from " + org.smartregister.chw.gbv.util.Constants.TABLES.GBV_REGISTER + " p " +
                                "              where p.is_closed is 0 ";
                return NavigationDao.getQueryCount(sqlGbv);
            case org.smartregister.chw.lab.util.Constants.TABLES.LAB_TEST_REQUESTS:
                String sqlLab =
                        "SELECT count(*) " +
                                "   from " + org.smartregister.chw.lab.util.Constants.TABLES.LAB_TEST_REQUESTS + " p " +
                                "              where p.patient_id is not null and p.results is not null and p.date_results_provided_to_client is null and p.is_closed is 0 ";
                return NavigationDao.getQueryCount(sqlLab);
            case CoreConstants.TABLE_NAME.GE_REGISTRATIONS:
                String sqlGe =
                        "SELECT count(*) " +
                                "   from " + CoreConstants.TABLE_NAME.GE_REGISTRATIONS + " g " +
                                "              where g.is_closed is 0 ";
                return NavigationDao.getQueryCount(sqlGe);
            default:
                return NavigationDao.getTableCount(tableName);
        }
    }

    private Long getLastCheckTimeStamp() {
        return coreApplication.getEcSyncHelper().getLastCheckTimeStamp();
    }
}