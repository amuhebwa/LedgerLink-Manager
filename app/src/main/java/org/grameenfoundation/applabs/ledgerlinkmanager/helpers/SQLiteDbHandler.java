package org.grameenfoundation.applabs.ledgerlinkmanager.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.grameenfoundation.applabs.ledgerlinkmanager.models.VslaInfo;

import java.util.ArrayList;
import java.util.List;

public class SQLiteDbHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Ledgerlink";
    private static final String TABLE_NAME = "VslaGroupsData";
    /** Table column names */
    private static final String ID = "Id";
    private static final String GROUPNAME = "GroupName";
    private static final String REPMEMBERNAME = "RepresentativeName";
    private static final String REPMEMBERPOST = "RepresentativePost";
    private static final String REPMEMBERPHONENUMBER = "RepresentativeContact";
    private static final String GROUPACCOUNTNUMBER = "GroupAccountNumber";
    private static final String PHYSICALADDRESS = "PhysicalAddress";
    private static final String REGIONNAME = "RegionName";
    private static final String LOCATIONCORDINATES = "LocationCordinates";
    private static final String ISSUEDPHONENUMBER = "IssuedPhoneNumber";
    private static final String ISDATASENT = "IsDataSent";
    private static final String SUPPORTTYPE = "SupportType";
    private static final String NUMBEROFCYCLES = "NumberOfCycles";
    private static final String IMPLEMENTERS = "Implementers";

    private static final String[] COLUMNS = {ID, GROUPNAME, REPMEMBERNAME, REPMEMBERPOST, REPMEMBERPHONENUMBER,
            GROUPACCOUNTNUMBER, PHYSICALADDRESS, REGIONNAME, LOCATIONCORDINATES, ISSUEDPHONENUMBER, ISDATASENT,
            SUPPORTTYPE, NUMBEROFCYCLES, IMPLEMENTERS};

    public SQLiteDbHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_DATABASE_LEDGERLINK = "CREATE TABLE " +
                TABLE_NAME + " ( " + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                GROUPNAME + " TEXT, " +
                REPMEMBERNAME + " TEXT, " +
                REPMEMBERPOST + " TEXT, " +
                REPMEMBERPHONENUMBER + " TEXT, " +
                GROUPACCOUNTNUMBER + " TEXT, " +
                PHYSICALADDRESS + " TEXT, " +
                REGIONNAME + " TEXT, " +
                LOCATIONCORDINATES + " TEXT, " +
                ISSUEDPHONENUMBER + " TEXT, " +
                ISDATASENT + " TEXT, " +
                SUPPORTTYPE + " TEXT, " +
                NUMBEROFCYCLES + " TEXT, " +
                IMPLEMENTERS + " TEXT " + " ) ";
        db.execSQL(CREATE_DATABASE_LEDGERLINK);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP IF TABLE EXISTS " + TABLE_NAME);
        onCreate(db);

    }

    /** ********CRUD OPERATIONS ******** */

    /**
     * Add a new group to the database
     * Returns the Id(PK) of the group that has just been added
     */
    public long addGroupData(VslaInfo vslaInfo) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(GROUPNAME, vslaInfo.getGroupName());
        values.put(REPMEMBERNAME, vslaInfo.getMemberName());
        values.put(REPMEMBERPOST, vslaInfo.getMemberPost());
        values.put(REPMEMBERPHONENUMBER, vslaInfo.getMemberPhoneNumber());
        values.put(GROUPACCOUNTNUMBER, vslaInfo.getGroupAccountNumber());
        values.put(PHYSICALADDRESS, vslaInfo.getPhysicalAddress());
        values.put(REGIONNAME, vslaInfo.getRegionName());
        values.put(LOCATIONCORDINATES, vslaInfo.getLocationCordinates());
        values.put(ISSUEDPHONENUMBER, vslaInfo.getIssuedPhoneNumber());
        values.put(ISDATASENT, vslaInfo.getIsDataSent());
        values.put(SUPPORTTYPE, vslaInfo.getSupportType());
        values.put(NUMBEROFCYCLES, vslaInfo.getNumberOfCycles());
        values.put(IMPLEMENTERS, vslaInfo.getImplementers());
        final long InsertedId = database.insert(TABLE_NAME, null, values);
        database.close();
        return InsertedId;
    }

    /** Query data base for a single group's information */
    public VslaInfo getGroupData(int id) {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.query(TABLE_NAME, COLUMNS, ID + "=?", new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
        }
        VslaInfo vslaInfo = new VslaInfo();
        vslaInfo.setId(Integer.parseInt(cursor.getString(0)));
        vslaInfo.setGroupName(cursor.getString(1));
        vslaInfo.setMemberName(cursor.getString(2));
        vslaInfo.setMemberPost(cursor.getString(3));
        vslaInfo.setMemberPhoneNumber(cursor.getString(4));
        vslaInfo.setGroupAccountNumber(cursor.getString(5));
        vslaInfo.setPhysicalAddress(cursor.getString(6));
        vslaInfo.setRegionName(cursor.getString(7));
        vslaInfo.setLocationCordinates(cursor.getString(8));
        vslaInfo.setIssuedPhoneNumber(cursor.getString(9));
        vslaInfo.setIsDataSent(cursor.getString(10));
        vslaInfo.setSupportType(cursor.getString(11));
        vslaInfo.setNumberOfCycles(cursor.getString(12));
        vslaInfo.setImplementers(cursor.getString(13));
        // cursor.close();
        return vslaInfo;
    }

    /** Query the database for a list of all groups */

    public List<VslaInfo> getAllGroups() {
        List<VslaInfo> allVslaGroups = new ArrayList<>();

        String SelectQuery = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(SelectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                VslaInfo vslaInfo = new VslaInfo();
                vslaInfo.setId(Integer.parseInt(cursor.getString(0)));
                vslaInfo.setGroupName(cursor.getString(1));
                vslaInfo.setMemberName(cursor.getString(2));
                vslaInfo.setMemberPost(cursor.getString(3));
                vslaInfo.setMemberPhoneNumber(cursor.getString(4));
                vslaInfo.setGroupAccountNumber(cursor.getString(5));
                vslaInfo.setPhysicalAddress(cursor.getString(6));
                vslaInfo.setRegionName(cursor.getString(7));
                vslaInfo.setLocationCordinates(cursor.getString(8));
                vslaInfo.setIssuedPhoneNumber(cursor.getString(9));
                vslaInfo.setIsDataSent(cursor.getString(10));
                vslaInfo.setSupportType(cursor.getString(11));
                vslaInfo.setNumberOfCycles(cursor.getString(12));
                vslaInfo.setImplementers(cursor.getString(13));
                allVslaGroups.add(vslaInfo);
            } while (cursor.moveToNext());
            //  cursor.close();
        }
        return allVslaGroups;
    }

    /** update information for a particular group */

    public int upDateGroupData(VslaInfo vslaInfo, long vslaDatabaseId) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(GROUPNAME, vslaInfo.getGroupName());
        values.put(REPMEMBERNAME, vslaInfo.getMemberName());
        values.put(REPMEMBERPOST, vslaInfo.getMemberPost());
        values.put(REPMEMBERPHONENUMBER, vslaInfo.getMemberPhoneNumber());
        values.put(GROUPACCOUNTNUMBER, vslaInfo.getGroupAccountNumber());
        values.put(PHYSICALADDRESS, vslaInfo.getPhysicalAddress());
        values.put(REGIONNAME, vslaInfo.getRegionName());
        values.put(LOCATIONCORDINATES, vslaInfo.getLocationCordinates());
        values.put(ISSUEDPHONENUMBER, vslaInfo.getIssuedPhoneNumber());
        values.put(ISDATASENT, vslaInfo.getIsDataSent());
        values.put(SUPPORTTYPE, vslaInfo.getSupportType());
        values.put(NUMBEROFCYCLES, vslaInfo.getNumberOfCycles());
        values.put(IMPLEMENTERS, vslaInfo.getImplementers());

        return database.update(TABLE_NAME, values, ID + " = ?", new String[]{String.valueOf(vslaDatabaseId)});
        // database.close();
    }

   /* public void deletGroupData(VslaInfo vslaDataModel) {
        SQLiteDatabase database = this.getWritableDatabase();
        database.delete(TABLE_NAME, ID + " = ?", new String[]{String.valueOf(vslaDataModel.getId())});
        database.close();
    }*/

    /** Check if a group exists in the  database */
    public boolean checkIfGroupExists(String vslaName) {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.query(TABLE_NAME,
                COLUMNS,
                GROUPNAME + "=?",
                new String[]{String.valueOf(vslaName)},
                null,
                null,
                null,
                null);
        // cursor.close();
        return cursor.moveToFirst();
    }
}
