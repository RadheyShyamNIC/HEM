package in.nic.hem;

import android.Manifest;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.ParcelFileDescriptor;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;

import in.nic.hem.db.DataBaseHelper;
import in.nic.hem.mypkg.AppPermission;
import in.nic.hem.mypkg.CommonData;
import in.nic.hem.mypkg.FileUtils;

import static android.app.Activity.RESULT_OK;

public class FragmentIO extends Fragment {
    Button btn_io_restore,btn_io_backup;
    CommonData commonData;
    Context context;
    private static final int CREATE_REQUEST_CODE = 40;
    private static final int OPEN_REQUEST_CODE = 41;
    private static final int SAVE_REQUEST_CODE = 42;

    public FragmentIO() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_io, container, false);
        btn_io_restore = (Button) view.findViewById(R.id.btn_io_restore);
        btn_io_backup = (Button) view.findViewById(R.id.btn_io_backup);
        btn_io_restore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFile(view);
//                if (clearPermissionRequest()){
//                    showFileChooser();
//                }
            }
        });
        btn_io_backup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newFile(view);
//                if (clearPermissionRequest()){
//                    try{
//                        new DataBaseHelper(context).BackupDatabase();
//                    }catch(Exception e){e.printStackTrace();}
//                }
            }
        });
        return view;
    }
    private boolean clearPermissionRequest()
    {
        AppPermission appPermission = new AppPermission(getContext(),getActivity());
        boolean permissions = appPermission.checkPermissionsFileAccess(true);
        if (!permissions) {
            permissions = appPermission.checkPermissionsFileAccess(false);
            Toast.makeText(getContext(), "Storage Access Permission Denied.... Give Permision to use this feature", Toast.LENGTH_LONG).show();
        }
        return permissions;
    }
    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        int REQUEST_CODE = 1001;
        startActivityForResult(intent, REQUEST_CODE);
    }
    private String getFilePath(Uri uri){
        String strFilePath = "";
        strFilePath = uri.getScheme();
        if(strFilePath.equals("content"))
        {
            String id = DocumentsContract.getDocumentId(uri);
            uri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
        }else if(strFilePath.equals("file")){
            strFilePath = uri.getPath();
        }
        return strFilePath;
    }
    /////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////
    public void newFile(View view)
    {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/vnd.sqlite3");
        intent.putExtra(Intent.EXTRA_TITLE, "PFD_dataBackup.db");
        startActivityForResult(intent, CREATE_REQUEST_CODE);
    }
    public void saveFile(View view)
    {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/vnd.sqlite3");
        startActivityForResult(intent, SAVE_REQUEST_CODE);
    }
    public void openFile(View view)
    {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/vnd.sqlite3");
        startActivityForResult(intent, OPEN_REQUEST_CODE);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent resultData) {
        if (resultCode == Activity.RESULT_OK)
        {
            Uri currentUri = null;
            if (resultCode == Activity.RESULT_OK)
            {
                if (requestCode == CREATE_REQUEST_CODE)
                {
                    if (resultData != null) {
                        currentUri = resultData.getData();
                        writeFileContent(currentUri);
                    }
                } else if (requestCode == SAVE_REQUEST_CODE) {

                    if (resultData != null) {
                        currentUri = resultData.getData();
                        writeFileContent(currentUri);
                    }
                } else if (requestCode == OPEN_REQUEST_CODE) {
                    //Restore Database
                    if (resultData != null) {
                        currentUri = resultData.getData();
                        try {
                            readFileContent(currentUri);
                            Toast.makeText(getContext(), "Data Restored successfully", Toast.LENGTH_LONG).show();
                        } catch (IOException e) {
                            // Handle error here
                        }
                    }
                }
            }
        }
    }
    private void readFileContent(Uri uri) throws IOException {
        ParcelFileDescriptor pfd = getActivity().getContentResolver().openFileDescriptor(uri, "r");
        FileInputStream inputStream = new FileInputStream(pfd.getFileDescriptor());
        new DataBaseHelper(context).restoreDatabaseBackup(inputStream);
        inputStream.close();
        pfd.close();
    }
    private void writeFileContent(Uri uri)
    {
        try{
            ParcelFileDescriptor pfd = getActivity().getContentResolver().openFileDescriptor(uri, "w");
            FileOutputStream fileOutputStream = new FileOutputStream(pfd.getFileDescriptor());
            new DataBaseHelper(context).createDatabaseBackup(fileOutputStream);
            fileOutputStream.close();
            pfd.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}