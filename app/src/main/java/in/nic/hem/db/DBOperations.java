package in.nic.hem.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import java.util.ArrayList;
import java.util.List;

import in.nic.hem.mypkg.DropDownItem;

public class DBOperations {
    private Context context;
    private DataBaseHelper dataBaseHelper;
    private SQLiteDatabase db;
    private SQLiteStatement stmt;
    private Cursor cursor;


    public DBOperations(Context context){
        this.context = context;
        dataBaseHelper = new DataBaseHelper(context);
    }
    public List<DropDownItem> getCategoryList(String first_option){
        List<DropDownItem> categoryDropdownList = new ArrayList<DropDownItem>();
        categoryDropdownList.add(new DropDownItem("-1", first_option));
        String sqlstr = "select met.id, met.expenditure_type from mas_expenditure_type met" +
                " inner join profile  p on met.profile_id = p.id" +
                " and p.isdefault=1 order by met.expenditure_type";
        db = dataBaseHelper.getDB_Connection();
        try
        {
            cursor = db.rawQuery(sqlstr,null);
            String id, txt;
            while(cursor.moveToNext())
            {
                id = cursor.getString(0);
                txt = cursor.getString(1);
                categoryDropdownList.add(new DropDownItem(id,txt));
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        finally {
            try{
                if(db!=null)
                {
                    db.close();
                }
            }
            catch (Exception ex){ex.printStackTrace();}
        }
        return categoryDropdownList;
    }
    public List<DropDownItem> getCategoryList(){
        List<DropDownItem> categoryDropdownList = new ArrayList<DropDownItem>();
        String sqlstr = "select met.id, met.expenditure_type from mas_expenditure_type met" +
                " inner join profile  p on met.profile_id = p.id" +
                " and p.isdefault=1 order by met.expenditure_type";
        db = dataBaseHelper.getDB_Connection();
        try
        {
            cursor = db.rawQuery(sqlstr,null);
            String id, txt;
            while(cursor.moveToNext())
            {
                id = cursor.getString(0);
                txt = cursor.getString(1);
                categoryDropdownList.add(new DropDownItem(id,txt));
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        finally {
            try{
                if(db!=null)
                {
                    db.close();
                }
            }
            catch (Exception ex){ex.printStackTrace();}
        }
        return categoryDropdownList;
    }
    public List<DropDownItem> getCategoryListExceptOne(int exceptionId){
        List<DropDownItem> categoryDropdownList = new ArrayList<DropDownItem>();
        String sqlstr = "select met.id, met.expenditure_type from mas_expenditure_type met" +
                " inner join profile  p on met.profile_id = p.id" +
                " and p.isdefault=1 and met.id<>"+ exceptionId +" order by met.expenditure_type";
        db = dataBaseHelper.getDB_Connection();
        try
        {
            cursor = db.rawQuery(sqlstr,null);
            String id, txt;
            while(cursor.moveToNext())
            {
                id = cursor.getString(0);
                txt = cursor.getString(1);
                categoryDropdownList.add(new DropDownItem(id,txt));
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        finally {
            try{
                if(db!=null)
                {
                    db.close();
                }
            }
            catch (Exception ex){ex.printStackTrace();}
        }
        return categoryDropdownList;
    }
    public List<DropDownItem> getProfileList(){
        List<DropDownItem> profileDropdownList = new ArrayList<DropDownItem>();
        String sqlstr = "select id, description from profile order by description";
        db = dataBaseHelper.getDB_Connection();
        try
        {
            cursor = db.rawQuery(sqlstr,null);
            String id, txt;
            while(cursor.moveToNext())
            {
                id = cursor.getString(0);
                txt = cursor.getString(1);
                profileDropdownList.add(new DropDownItem(id,txt));
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        finally {
            try{
                if(db!=null)
                {
                    db.close();
                }
            }
            catch (Exception ex){ex.printStackTrace();}
        }
        return profileDropdownList;
    }
    public List<DropDownItem> getProfileListExceptOne(int exceptionId){
        List<DropDownItem> profileDropdownList = new ArrayList<DropDownItem>();
        String sqlstr = "select id, description from profile where id<>"+exceptionId+" order by description";
        db = dataBaseHelper.getDB_Connection();
        try
        {
            cursor = db.rawQuery(sqlstr,null);
            String id, txt;
            profileDropdownList.add(new DropDownItem("-1","-- Select Profile to Merge With -"));
            while(cursor.moveToNext())
            {
                id = cursor.getString(0);
                txt = cursor.getString(1);
                profileDropdownList.add(new DropDownItem(id,txt));
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        finally {
            try{
                if(db!=null)
                {
                    db.close();
                }
            }
            catch (Exception ex){ex.printStackTrace();}
        }
        return profileDropdownList;
    }
    public List<String[]> getRecentlyAddedExpenditureDeta(){
        List<String[]> recentExpenditureEntryDetails = new ArrayList<String[]>();
        String sqlstr = "select et.transaction_date, et.expenditure_amount, mt.expenditure_type, et.description, coalesce(et.image_attachment_id,-1),et.id,et.profile_id" +
                " from expenditure_transaction et" +
                " inner join mas_expenditure_type mt on mt.id=et.category_id and mt.profile_id = et.profile_id" +
                " inner join profile p on p.id = mt.profile_id" +
                " where p.isdefault=1 order by et.entry_date desc limit 10";
        db = dataBaseHelper.getDB_Connection();
        try
        {
            cursor = db.rawQuery(sqlstr,null);
            String id, txt;
            while(cursor.moveToNext())
            {
                String[] item = new String[7];
                item[0] = getComputerDate2UserDate(cursor.getString(0));
                item[1] = cursor.getString(1);
                item[2] = cursor.getString(2);
                item[3] = cursor.getString(3);
                item[4] = cursor.getString(4);
                item[5] = cursor.getString(5);
                item[6] = cursor.getString(6);
                recentExpenditureEntryDetails.add(item);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        finally {
            try{
                if(db!=null)
                {
                    db.close();
                }
            }
            catch (Exception ex){ex.printStackTrace();}
        }
        return recentExpenditureEntryDetails;
    }
    public String[] getOneExpenditureData(String item_id){
        String[] expenditureEntryDetails = new String[7];
        String sqlstr = "select et.transaction_date, et.expenditure_amount, et.category_id , et.description, coalesce(et.image_attachment_id,-1),et.id,et.profile_id" +
                " from expenditure_transaction et" +
                " inner join profile p on p.id = et.profile_id" +
                " where p.isdefault=1 AND et.id="+item_id;
        db = dataBaseHelper.getDB_Connection();
        try
        {
            cursor = db.rawQuery(sqlstr,null);
            String id, txt;
            while(cursor.moveToNext())
            {
                expenditureEntryDetails[0] = getComputerDate2UserDate(cursor.getString(0));
                expenditureEntryDetails[1] = cursor.getString(1);
                expenditureEntryDetails[2] = cursor.getString(2);
                expenditureEntryDetails[3] = cursor.getString(3);
                expenditureEntryDetails[4] = cursor.getString(4);
                expenditureEntryDetails[5] = cursor.getString(5);
                expenditureEntryDetails[6] = cursor.getString(6);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        finally {
            try{
                if(db!=null)
                {
                    db.close();
                }
            }
            catch (Exception ex){ex.printStackTrace();}
        }
        return expenditureEntryDetails;
    }
    public List<String[]> getAvailableProfilesDeta(){
        List<String[]> availableProfilesData = new ArrayList<String[]>();
        String sqlstr = "select id, description, coalesce(isdefault,'0')  from profile ORDER BY description ASC";
        db = dataBaseHelper.getDB_Connection();
        try
        {
            cursor = db.rawQuery(sqlstr,null);
            String id, txt;
            while(cursor.moveToNext())
            {
                String[] item = new String[3];
                item[0] = cursor.getString(0);
                item[1] = cursor.getString(1);
                item[2] = cursor.getString(2);
                availableProfilesData.add(item);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        finally {
            try{
                if(db!=null)
                {
                    db.close();
                }
            }
            catch (Exception ex){ex.printStackTrace();}
        }
        return availableProfilesData;
    }
    public List<String[]> getComparedData(){
        List<String[]> dataComparisionReport = new ArrayList<String[]>();
        String amountPreviousMonth;
        String expenditureCategoryID,expenditureCategoryName;
        int i=0,j=0;
        String sqlstr1 = "select et.category_id, met.expenditure_type, sum(et.expenditure_amount) sumamount " +
                "from expenditure_transaction et " +
                "inner join mas_expenditure_type met ON et.category_id = met.id  AND et.profile_id = met.profile_id " +
                "inner join profile p ON p.id = et.profile_id " +
                "WHERE p.isdefault=1 AND et.transaction_date>= date('now', 'start of month') " +
                "GROUP BY et.category_id, met.expenditure_type " +
                "ORDER BY sumamount DESC";
        String sqlstr2 = "select et.category_id, met.expenditure_type, sum(et.expenditure_amount) sumamount " +
                "from expenditure_transaction et " +
                "inner join mas_expenditure_type met ON et.category_id = met.id  AND et.profile_id = met.profile_id " +
                "inner join profile p ON p.id = et.profile_id " +
                "WHERE p.isdefault=1 AND et.transaction_date>= date('now', 'start of month','-1 month') AND et.transaction_date <date('now', 'start of month') " +
                "GROUP BY et.category_id, met.expenditure_type " +
                "ORDER BY sumamount DESC";
        db = dataBaseHelper.getDB_Connection();
        try
        {
            cursor = db.rawQuery(sqlstr1,null);
            while(cursor.moveToNext())
            {
                String[] item = new String[4];
                item[0] = cursor.getString(0);//category ID
                item[1] = cursor.getString(1); //Category Type
                item[2] = "0";   //Previous Month Data
                item[3] = cursor.getString(2); // Current Month Data
                dataComparisionReport.add(item);
                i++;
            }
            cursor = db.rawQuery(sqlstr2,null);
            while (cursor.moveToNext()){
                expenditureCategoryID= cursor.getString(0);
                expenditureCategoryName = cursor.getString(1);
                amountPreviousMonth=cursor.getString(2);
                boolean flag=false;
                for(j=0;j<i;j++)
                {
                    if(((dataComparisionReport.get(j))[0]).equals(expenditureCategoryID))
                    {
                        flag=true;
                        break;
                    }
                }
                if(!flag)
                {
                    dataComparisionReport.add(new String[]{expenditureCategoryID,expenditureCategoryName,amountPreviousMonth,"0"});
                }
                else
                {
                    String[] expenditureRowData = dataComparisionReport.get(j);
                    expenditureRowData[2]= amountPreviousMonth;
                    dataComparisionReport.set(j, expenditureRowData);
                }
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        finally {
            try{
                if(db!=null)
                {
                    db.close();
                }
            }
            catch (Exception ex){ex.printStackTrace();}
        }
        return dataComparisionReport;
    }
    public String[] getComparedDataSum(){
        String[] dataComparisionReport = new String[2];
        dataComparisionReport[0]="0";
        dataComparisionReport[1]="0";
        String sqlstr1 = "select coalesce(sum(et.expenditure_amount),0) sumamount " +
                "from expenditure_transaction et " +
                "inner join mas_expenditure_type met ON et.category_id = met.id  AND et.profile_id = met.profile_id " +
                "inner join profile p ON p.id = et.profile_id " +
                "WHERE p.isdefault=1 AND et.transaction_date>= date('now', 'start of month') ";
        String sqlstr2 = "select coalesce(sum(et.expenditure_amount),0) sumamount " +
                "from expenditure_transaction et " +
                "inner join mas_expenditure_type met ON et.category_id = met.id  AND et.profile_id = met.profile_id " +
                "inner join profile p ON p.id = et.profile_id " +
                "WHERE p.isdefault=1 AND et.transaction_date>= date('now', 'start of month','-1 month') AND et.transaction_date <date('now', 'start of month') ";
        db = dataBaseHelper.getDB_Connection();
        try
        {
            cursor = db.rawQuery(sqlstr1,null);
            if(cursor.moveToNext())
            {
                dataComparisionReport[1] = cursor.getString(0);
            }
            cursor = db.rawQuery(sqlstr2,null);
            while (cursor.moveToNext()){
                dataComparisionReport[0] = cursor.getString(0);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        finally {
            try{
                if(db!=null)
                {
                    db.close();
                }
            }
            catch (Exception ex){ex.printStackTrace();}
        }
        return dataComparisionReport;
    }
    public List<String[]> getComparedDataYearwise(){
        List<String[]> dataComparisionReport = new ArrayList<String[]>();
        String amountPreviousMonth;
        String expenditureCategoryID,expenditureCategoryName;
        int i=0,j=0;
        String sqlstr1 = "select et.category_id, met.expenditure_type, sum(et.expenditure_amount) sumamount " +
                "from expenditure_transaction et " +
                "inner join mas_expenditure_type met ON et.category_id = met.id  AND et.profile_id = met.profile_id " +
                "inner join profile p ON p.id = et.profile_id " +
                "WHERE p.isdefault=1 AND et.transaction_date>= date('now', 'start of year') " +
                "GROUP BY et.category_id, met.expenditure_type " +
                "ORDER BY sumamount DESC";
        String sqlstr2 = "select et.category_id, met.expenditure_type, sum(et.expenditure_amount) sumamount " +
                "from expenditure_transaction et " +
                "inner join mas_expenditure_type met ON et.category_id = met.id  AND et.profile_id = met.profile_id " +
                "inner join profile p ON p.id = et.profile_id " +
                "WHERE p.isdefault=1 AND et.transaction_date>= date('now', 'start of year','-1 year') AND et.transaction_date <date('now', 'start of year') " +
                "GROUP BY et.category_id, met.expenditure_type " +
                "ORDER BY sumamount DESC";
        db = dataBaseHelper.getDB_Connection();
        try
        {
            cursor = db.rawQuery(sqlstr1,null);
            while(cursor.moveToNext())
            {
                String[] item = new String[4];
                item[0] = cursor.getString(0);//category ID
                item[1] = cursor.getString(1); //Category Type
                item[2] = "0";   //Previous Month Data
                item[3] = cursor.getString(2); // Current Month Data
                dataComparisionReport.add(item);
                i++;
            }
            cursor = db.rawQuery(sqlstr2,null);
            while (cursor.moveToNext()){
                expenditureCategoryID= cursor.getString(0);
                expenditureCategoryName = cursor.getString(1);
                amountPreviousMonth=cursor.getString(2);
                boolean flag=false;
                for(j=0;j<i;j++)
                {
                    if(((dataComparisionReport.get(j))[0]).equals(expenditureCategoryID))
                    {
                        flag=true;
                        break;
                    }
                }
                if(!flag)
                {
                    dataComparisionReport.add(new String[]{expenditureCategoryID,expenditureCategoryName,amountPreviousMonth,"0"});
                }
                else
                {
                    String[] expenditureRowData = dataComparisionReport.get(j);
                    expenditureRowData[2]= amountPreviousMonth;
                    dataComparisionReport.set(j, expenditureRowData);
                }
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        finally {
            try{
                if(db!=null)
                {
                    db.close();
                }
            }
            catch (Exception ex){ex.printStackTrace();}
        }
        return dataComparisionReport;
    }
    public String[] getComparedDataSumYearwise(){
        String[] dataComparisionReport = new String[2];
        dataComparisionReport[0]="0";
        dataComparisionReport[1]="0";
        String sqlstr1 = "select coalesce(sum(et.expenditure_amount),0) sumamount " +
                "from expenditure_transaction et " +
                "inner join mas_expenditure_type met ON et.category_id = met.id  AND et.profile_id = met.profile_id " +
                "inner join profile p ON p.id = et.profile_id " +
                "WHERE p.isdefault=1 AND et.transaction_date>= date('now', 'start of year') ";
        String sqlstr2 = "select coalesce(sum(et.expenditure_amount),0) sumamount " +
                "from expenditure_transaction et " +
                "inner join mas_expenditure_type met ON et.category_id = met.id  AND et.profile_id = met.profile_id " +
                "inner join profile p ON p.id = et.profile_id " +
                "WHERE p.isdefault=1 AND et.transaction_date>= date('now', 'start of year','-1 year') AND et.transaction_date <date('now', 'start of year') ";
        db = dataBaseHelper.getDB_Connection();
        try
        {
            cursor = db.rawQuery(sqlstr1,null);
            if(cursor.moveToNext())
            {
                dataComparisionReport[1] = cursor.getString(0);
            }
            cursor = db.rawQuery(sqlstr2,null);
            while (cursor.moveToNext()){
                dataComparisionReport[0] = cursor.getString(0);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        finally {
            try{
                if(db!=null)
                {
                    db.close();
                }
            }
            catch (Exception ex){ex.printStackTrace();}
        }
        return dataComparisionReport;
    }
    public String getDefaultProfileName(){
        String returnData = "";
        String sqlstr = "select description  from profile  WHERE isdefault=1";
        db = dataBaseHelper.getDB_Connection();
        try
        {
            cursor = db.rawQuery(sqlstr,null);
            String id, txt;
            if(cursor.moveToNext())
            {
                returnData = cursor.getString(0);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        finally {
            try{
                if(db!=null)
                {
                    db.close();
                }
            }
            catch (Exception ex){ex.printStackTrace();}
        }
        return returnData;
    }
    public int getDefaultProfileID(){
        int returnData = -1;
        String sqlstr = "select id  from profile  WHERE isdefault=1";
        db = dataBaseHelper.getDB_Connection();
        try
        {
            cursor = db.rawQuery(sqlstr,null);
            String id, txt;
            if(cursor.moveToNext())
            {
                returnData = cursor.getInt(0);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        finally {
            try{
                if(db!=null)
                {
                    db.close();
                }
            }
            catch (Exception ex){ex.printStackTrace();}
        }
        return returnData;
    }
    public void setDefaultProfile(String id){
        String sqlstr1 = "update profile set isdefault = 0 where id<> "+id;
        String sqlstr2 = "update profile set isdefault = 1 where id= "+id;
        db = dataBaseHelper.getDB_Connection();
        try
        {
            db.execSQL(sqlstr1);
            db.execSQL(sqlstr2);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        finally {
            try{
                if(db!=null)
                {
                    db.close();
                }
            }
            catch (Exception ex){ex.printStackTrace();}
        }
    }
    public boolean saveNewExpenditureCategory(String expenditure_category, int action_category_id ) {
        boolean result = false;
        try {
            String sqlstr = "insert into mas_expenditure_type (expenditure_type, profile_id) select '"+expenditure_category+"', id from profile where isdefault=1";
            if(action_category_id>0){
                sqlstr = "update mas_expenditure_type set expenditure_type ='"+expenditure_category+"' WHERE id="+action_category_id +" AND profile_id= (select id from profile where isdefault=1)";
            }
            db = dataBaseHelper.getDB_Connection();
            db.execSQL(sqlstr);
            result = true;
        } catch (Exception ex) {
            ex.printStackTrace();
            result = false;
        }
        return result;
    }
    public boolean saveNewExpenditureProfile(String expenditure_profile, int action_profile_id) {
        boolean result = false;
        try {
            String sqlstr = "insert into profile (description) VALUES ('"+expenditure_profile+"')";
            if(action_profile_id>0){
                sqlstr = "update profile set description ='"+expenditure_profile+"' WHERE id="+action_profile_id;
            }
            db = dataBaseHelper.getDB_Connection();
            db.execSQL(sqlstr);
            result = true;
        } catch (Exception ex) {
            ex.printStackTrace();
            result = false;
        }
        return result;
    }

    public boolean saveNewExpenditureRecord(int category_id, String amount, String date, String description, byte[] imageByteArray) {
        boolean result = false;
        String sqlstr1 = "insert into image (profile_id, category_id, img) values ((select id from profile where isdefault=1),?, ?);";
        String sqlstr2 = "select last_insert_rowid();";
        String sqlstr3 = "insert into expenditure_transaction (profile_id,entry_date, category_id, expenditure_amount,description,transaction_date, image_attachment_id)\n" +
                "values((select id from profile where isdefault=1),CURRENT_TIMESTAMP,?,?,?,?,?)";
        int image_id = -1;
        db = dataBaseHelper.getDB_Connection();
        db.beginTransaction();
        try
        {
            if(imageByteArray!=null) {
                stmt = db.compileStatement(sqlstr1);
                stmt.bindLong(1, category_id);
                stmt.bindBlob(2, imageByteArray);
                stmt.execute();
                cursor = db.rawQuery(sqlstr2,null);
                if(cursor.moveToNext())
                {
                    image_id = cursor.getInt(0);
                }
            }
            stmt = db.compileStatement(sqlstr3);
            stmt.bindLong(1,category_id);
            stmt.bindLong(2,Integer.parseInt(amount));
            stmt.bindString(3,description);
            stmt.bindString(4,getUserDate2ComputerDate(date));
            if(image_id<0){
                stmt.bindNull(5);
            }else{
                stmt.bindLong(5,image_id);
            }
            long x = stmt.executeInsert();
            //stmt.execute();
            x=x+1;
            db.setTransactionSuccessful();
            result = true;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        finally {
            db.endTransaction();
            try{
                if(stmt!=null)
                {
                    stmt.close();
                }
                if(db!=null)
                {
                    db.close();
                }
            }
            catch (Exception ex){ex.printStackTrace();}
        }
        return  result;
    }

    public byte[] getImageBytes(String img_id) {
        byte[] returnData = null;
        String sqlstr = "select img from image where attachment_id="+img_id;
        db = dataBaseHelper.getDB_Connection();
        try
        {
            cursor = db.rawQuery(sqlstr,null);
            if(cursor.moveToNext())
            {
                returnData = cursor.getBlob(0);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        finally {
            try{
                if(db!=null)
                {
                    db.close();
                }
            }
            catch (Exception ex){ex.printStackTrace();}
        }
        return returnData;
    }

    public List<String[]> getReport(int category_id, String date1, String date2, String amount1, String amount2, String description, String attachment_filter) {
        List<String[]> recentExpenditureEntryDetails = new ArrayList<String[]>();
        String filter_query = "";
        if(category_id>0){
            filter_query = filter_query + " AND et.category_id = "+category_id;
        }if(amount1.length()>0){
            filter_query = filter_query + " AND et.expenditure_amount >= "+amount1;
        }if(amount2.length()>0){
            filter_query = filter_query + " AND et.expenditure_amount <= "+amount2;
        }if(description.length()>0){
            filter_query = filter_query + " AND et.description LIKE '%"+description+"%'";
        }if(attachment_filter.equals("Yes")){
            filter_query = filter_query + " AND et.image_attachment_id is not NULL";
        }else if(attachment_filter.equals("No")){
            filter_query = filter_query + " AND et.image_attachment_id is NULL";
        }
        String sqlstr = "select et.transaction_date, et.expenditure_amount, mt.expenditure_type, et.description, coalesce(et.image_attachment_id,-1),et.id,et.profile_id" +
                " from expenditure_transaction et" +
                " inner join mas_expenditure_type mt on mt.id=et.category_id and mt.profile_id = et.profile_id" +
                " inner join profile p on p.id = mt.profile_id" +
                " where p.isdefault=1 AND et.transaction_date>='" +getUserDate2ComputerDate(date1)+"' AND et.transaction_date <='"+getUserDate2ComputerDate(date2)+"' "+ filter_query+
                " order by et.transaction_date ASC";
        db = dataBaseHelper.getDB_Connection();
        try
        {
            cursor = db.rawQuery(sqlstr,null);
            String id, txt;
            while(cursor.moveToNext())
            {
                String[] item = new String[7];
                item[0] = getComputerDate2UserDate(cursor.getString(0));
                item[1] = cursor.getString(1);
                item[2] = cursor.getString(2);
                item[3] = cursor.getString(3);
                item[4] = cursor.getString(4);
                item[5] = cursor.getString(5);
                item[6] = cursor.getString(6);
                recentExpenditureEntryDetails.add(item);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        finally {
            try{
                if(db!=null)
                {
                    db.close();
                }
            }
            catch (Exception ex){ex.printStackTrace();}
        }
        return recentExpenditureEntryDetails;
    }
    public int getReportAmountSum(int category_id, String date1, String date2, String amount1, String amount2, String description, String attachment_filter) {
        int sumAmount = 0;
        String filter_query = "";
        if(category_id>0){
            filter_query = filter_query + " AND et.category_id = "+category_id;
        }if(amount1.length()>0){
            filter_query = filter_query + " AND et.expenditure_amount >= "+amount1;
        }if(amount2.length()>0){
            filter_query = filter_query + " AND et.expenditure_amount <= "+amount2;
        }if(description.length()>0){
            filter_query = filter_query + " AND et.description LIKE '%"+description+"%'";
        }if(attachment_filter.equals("Yes")){
            filter_query = filter_query + " AND et.image_attachment_id is not NULL";
        }else if(attachment_filter.equals("No")){
            filter_query = filter_query + " AND et.image_attachment_id is NULL";
        }
        String sqlstr = "select sum(et.expenditure_amount) " +
                " from expenditure_transaction et" +
                " inner join mas_expenditure_type mt on mt.id=et.category_id and mt.profile_id = et.profile_id" +
                " inner join profile p on p.id = mt.profile_id" +
                " where p.isdefault=1 AND et.transaction_date>='" +getUserDate2ComputerDate(date1)+"' AND et.transaction_date <='"+getUserDate2ComputerDate(date2)+"' "+ filter_query+
                " order by et.transaction_date ASC";
        db = dataBaseHelper.getDB_Connection();
        try
        {
            cursor = db.rawQuery(sqlstr,null);
            String id, txt;
            if(cursor.moveToNext())
            {
                sumAmount = cursor.getInt(0);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        finally {
            try{
                if(db!=null)
                {
                    db.close();
                }
            }
            catch (Exception ex){ex.printStackTrace();}
        }
        return sumAmount;
    }
    private String getUserDate2ComputerDate(String date){
        String[] strArray = date.split("/");
        date = strArray[2]+"-"+strArray[1]+"-"+strArray[0];
        return date;
    }
    private String getComputerDate2UserDate(String date){
        String[] strArray = date.split("-");
        date = strArray[2]+"/"+strArray[1]+"/"+strArray[0];
        return date;
    }
    public void deleteCategory(int categoryID) {
        String sqlstr1 = "DELETE FROM mas_expenditure_type WHERE id="+categoryID + " AND profile_id=(select id from profile where isdefault=1)";
        String sqlstr2 = "DELETE FROM image WHERE profile_id=(select id from profile where isdefault=1) AND category_id = "+ categoryID;
        String sqlstr3 = "DELETE FROM expenditure_transaction WHERE category_id="+categoryID + " AND profile_id=(select id from profile where isdefault=1)";
        db = dataBaseHelper.getDB_Connection();
        try
        {
            db.execSQL(sqlstr3);
            db.execSQL(sqlstr2);
            db.execSQL(sqlstr1);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        finally {
            try{
                if(db!=null)
                {
                    db.close();
                }
            }
            catch (Exception ex){ex.printStackTrace();}
        }
    }
    public void deleteCategoryAndMergeEntries(int categoryID, int merge_into_categoryId) {
        String sqlstr1 = "DELETE FROM mas_expenditure_type WHERE id="+categoryID + " AND profile_id=(select id from profile where isdefault=1)";
        String sqlstr2 = "UPDATE image SET category_id="+merge_into_categoryId+" WHERE category_id="+categoryID+ " AND profile_id=(select id from profile where isdefault=1)";;
        String sqlstr3 = "UPDATE expenditure_transaction SET category_id="+merge_into_categoryId+" WHERE category_id="+categoryID+ " AND profile_id=(select id from profile where isdefault=1)";
        db = dataBaseHelper.getDB_Connection();
        try
        {
            db.execSQL(sqlstr3);
            db.execSQL(sqlstr2);
            db.execSQL(sqlstr1);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        finally {
            try{
                if(db!=null)
                {
                    db.close();
                }
            }
            catch (Exception ex){ex.printStackTrace();}
        }
    }


    public void deleteProfile(int profileID) {
        String sqlstr1 = "DELETE FROM profile WHERE id="+profileID;
        String sqlstr2 = "DELETE FROM mas_expenditure_type WHERE profile_id="+profileID;
        String sqlstr3 = "DELETE FROM image WHERE profile_id="+profileID;
        String sqlstr4 = "DELETE FROM expenditure_transaction WHERE profile_id="+profileID;
        db = dataBaseHelper.getDB_Connection();
        try
        {
            db.execSQL(sqlstr4);
            db.execSQL(sqlstr3);
            db.execSQL(sqlstr2);
            db.execSQL(sqlstr1);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        finally {
            try{
                if(db!=null)
                {
                    db.close();
                }
            }
            catch (Exception ex){ex.printStackTrace();}
        }
    }

    public void deleteProfileAndMergeEntries(int action_profile_id, int merge_into_profileId) {
        String sqlstr1 = "DELETE FROM profile WHERE id="+action_profile_id;
        String sqlstr2 = "UPDATE  mas_expenditure_type SET profile_id="+merge_into_profileId+" WHERE profile_id="+action_profile_id;
        String sqlstr3 = "UPDATE image SET profile_id="+merge_into_profileId+" WHERE profile_id="+action_profile_id;
        String sqlstr4 = "UPDATE expenditure_transaction SET profile_id="+merge_into_profileId+" WHERE profile_id="+action_profile_id;
        db = dataBaseHelper.getDB_Connection();
        try
        {
            db = dataBaseHelper.getDB_Connection();
            db.execSQL(sqlstr4);
            db.execSQL(sqlstr3);
            db.execSQL(sqlstr2);
            db.execSQL(sqlstr1);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        finally {
            try{
                if(db!=null)
                {
                    db.close();
                }
            }
            catch (Exception ex){ex.printStackTrace();}
        }
    }

    public boolean updateExpenditureRecord(String profile_id, String item_id, int category_id, String amount, String date, String description, byte[] imageByteArray, int captured_fresh_image, int saved_image_removed) {
        boolean result = false;
        int image_id = -1;
        db = dataBaseHelper.getDB_Connection();
        db.beginTransaction();
        try
        {
            String sqlstr = "select coalesce(image_attachment_id,-1) from expenditure_transaction WHERE id = "+item_id+" and profile_id = "+profile_id;
            cursor = db.rawQuery(sqlstr,null);
            if(cursor.moveToNext())
            {
                image_id = cursor.getInt(0);
            }
            if(saved_image_removed==1){
                ///Remove alreaddy saved image
                String sqlstr1 = "Update expenditure_transaction set image_attachment_id = null WHERE id = "+item_id+" and profile_id = "+profile_id;
                String sqlstr2 = "DELETE from image WHERE attachment_id=?";
                db.execSQL(sqlstr1);
                stmt = db.compileStatement(sqlstr2);
                stmt.bindLong(1, image_id);
                stmt.execute();
                image_id=-1;
            }
            if(captured_fresh_image==1){
                //Save New image
                String sqlstr1 = "insert into image (profile_id, category_id, img) values ((select id from profile where isdefault=1),?, ?);";
                String sqlstr2 = "select last_insert_rowid();";
                if(imageByteArray!=null) {
                    stmt = db.compileStatement(sqlstr1);
                    stmt.bindLong(1, category_id);
                    stmt.bindBlob(2, imageByteArray);
                    stmt.execute();
                    cursor = db.rawQuery(sqlstr2,null);
                    if(cursor.moveToNext())
                    {
                        image_id = cursor.getInt(0);
                    }
                }
            }
            /////////////Update data
            sqlstr = "Update expenditure_transaction set category_id=?, expenditure_amount=?, description=?, transaction_date=?, image_attachment_id=? WHERE profile_id="+profile_id+ " AND id = "+item_id;
            stmt = db.compileStatement(sqlstr);
            stmt.bindLong(1,category_id);
            stmt.bindLong(2,Integer.parseInt(amount));
            stmt.bindString(3,description);
            stmt.bindString(4,getUserDate2ComputerDate(date));
            if(image_id<0){
                stmt.bindNull(5);
            }else{
                stmt.bindLong(5,image_id);
            }
            stmt.executeInsert();
            db.setTransactionSuccessful();
            result = true;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        finally {
            db.endTransaction();
            try{
                if(stmt!=null)
                {
                    stmt.close();
                }
                if(db!=null)
                {
                    db.close();
                }
            }
            catch (Exception ex){ex.printStackTrace();}
        }
        return result;
    }

    public boolean deleteSingleEntry(String item_id, String profile_id) {
        boolean result = false;
        int image_id = -1;
        db = dataBaseHelper.getDB_Connection();
        db.beginTransaction();
        try
        {
            String sqlstr = "select coalesce(image_attachment_id,-1) from expenditure_transaction WHERE id = "+item_id+" and profile_id = "+profile_id;
            cursor = db.rawQuery(sqlstr,null);
            if(cursor.moveToNext())
            {
                image_id = cursor.getInt(0);
            }
            if(image_id>0){
                ///Remove alreaddy saved image
                String sqlstr1 = "Delete FROM expenditure_transaction  WHERE id = "+item_id+" and profile_id = "+profile_id;
                String sqlstr2 = "DELETE from image WHERE attachment_id=?";
                db.execSQL(sqlstr1);
                stmt = db.compileStatement(sqlstr2);
                stmt.bindLong(1, image_id);
                stmt.execute();
            }else{
                String sqlstr1 = "Delete FROM expenditure_transaction  WHERE id = "+item_id+" and profile_id = "+profile_id;
                stmt = db.compileStatement(sqlstr1);
                stmt.execute();
            }
            db.setTransactionSuccessful();
            result = true;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        finally {
            db.endTransaction();
            try{
                if(stmt!=null)
                {
                    stmt.close();
                }
                if(db!=null)
                {
                    db.close();
                }
            }
            catch (Exception ex){ex.printStackTrace();}
        }
        return result;
    }
}
