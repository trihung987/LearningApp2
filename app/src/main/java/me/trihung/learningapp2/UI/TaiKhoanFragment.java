package me.trihung.learningapp2.UI;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import me.trihung.learningapp2.DB.Database;
import me.trihung.learningapp2.Login;
import me.trihung.learningapp2.MainActivity;
import me.trihung.learningapp2.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TaiKhoanFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TaiKhoanFragment extends Fragment {

    // Fragment parameters
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    // UI Components
    private TextView tvTenTaiKhoan, tvTitleTenTaiKhoan, tvNgaySinh, tvGioiTinh,
            tvSoDienThoai, tvUserName, tvPassword, tvUsername;
    private ImageView ivProfilePic, btnEditProfile;
    private MaterialButton btnDoiThongTinCaNhan, btnDoiPassword, btnDangXuat;

    // Activity reference
    private MainActivity mMainActivity;

    // Database
    private Database db;

    // User data
    private String username;
    private String password;
    private String idND;
    private String HoTen;
    private String ngaySinh;
    private String gioiTinh;
    private String soDienThoai;

    public TaiKhoanFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TaiKhoanFragment.
     */
    public static TaiKhoanFragment newInstance(String param1, String param2) {
        TaiKhoanFragment fragment = new TaiKhoanFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_taikhoan, container, false);

        // Initialize views
        initializeViews(view);

        // Initialize database
        db = new Database(getContext());

        // Get reference to main activity
        mMainActivity = (MainActivity) getActivity();
        Log.d("maTK", "TAI KHOAN: " + mMainActivity.getmTk());

        // Load user data
        getThongTinNguoiDung(mMainActivity.getmTk());

        // Set click listeners
        setupListeners();

        return view;
    }

    private void initializeViews(View view) {
        // TextViews
        tvTenTaiKhoan = view.findViewById(R.id.tvTenTaiKhoan);
        tvTitleTenTaiKhoan = view.findViewById(R.id.tvTitleTenTaiKhoan);
        tvNgaySinh = view.findViewById(R.id.tvNgaySinh);
        tvGioiTinh = view.findViewById(R.id.tvGioiTinh);
        tvSoDienThoai = view.findViewById(R.id.tvSoDienThoai);
        tvUserName = view.findViewById(R.id.tvUserName);
        tvPassword = view.findViewById(R.id.tvPassword);
        tvUsername = view.findViewById(R.id.tvUsername);

        // ImageViews
        ivProfilePic = view.findViewById(R.id.ivProfilePic);
        btnEditProfile = view.findViewById(R.id.btnEditProfile);

        // Buttons
        btnDoiThongTinCaNhan = view.findViewById(R.id.btnDoiThongTinCaNhan);
        btnDoiPassword = view.findViewById(R.id.btnDoiPassword);
        btnDangXuat = view.findViewById(R.id.btnDangXuat);
    }

    private void setupListeners() {
        // Edit profile button
        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chinhSua();
            }
        });

        // Edit personal info button
        btnDoiThongTinCaNhan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chinhSua();
            }
        });

        // Change password button
        btnDoiPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chinhSuaPassword();
            }
        });

        // Logout button
        btnDangXuat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLogoutConfirmation();
            }
        });
    }

    private void showLogoutConfirmation() {
        AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
        alertDialog.setTitle("Đăng xuất");
        alertDialog.setIcon(R.drawable.question_mark);
        alertDialog.setMessage("Bạn có chắc chắn muốn đăng xuất?");

        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Đồng ý", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(getContext(), Login.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                alertDialog.cancel();
            }
        });

        alertDialog.show();
    }

    private void chinhSuaPassword() {
        // Create and show the custom password change dialog
        ChangePasswordDialog dialog = new ChangePasswordDialog(
                getContext(),
                username,
                password,
                new ChangePasswordDialog.OnPasswordChangedListener() {
                    @Override
                    public void onPasswordChanged() {
                        // Reload user data after password change
                        getThongTinNguoiDung(mMainActivity.getmTk());
                    }
                });
        dialog.show();
    }

    private void chinhSua() {
        // Inflate the custom layout for the dialog
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.dialog_edit_profile_layout, null);

        // Create a full-screen dialog using MaterialComponents
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext(),
                R.style.Theme_MyApplication_PopupOverlay);
        builder.setView(dialogView);

        // Find views in the dialog layout
        final EditText editTextHoTen = dialogView.findViewById(R.id.editTextHoTen);
        final TextView tvNgaySinh = dialogView.findViewById(R.id.tvNgaySinh);
        final Spinner spinnerGioiTinh = dialogView.findViewById(R.id.spinnerGioiTinh);
        final EditText editTextSoDienThoai = dialogView.findViewById(R.id.editTextSoDienThoai);
        final MaterialButton btnCancel = dialogView.findViewById(R.id.btnProfileCancel);
        final MaterialButton btnUpdate = dialogView.findViewById(R.id.btnProfileUpdate);

        // Set up the gender spinner
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, new String[]{"Nam", "Nữ"});
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGioiTinh.setAdapter(adapter);

        // Set current values to form fields
        editTextHoTen.setText(HoTen);
        tvNgaySinh.setText(ngaySinh);
        spinnerGioiTinh.setSelection(gioiTinh.equals("1") ? 0 : 1);
        editTextSoDienThoai.setText(soDienThoai);

        // Parse the existing date
        final Calendar calendar = Calendar.getInstance();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date date = sdf.parse(ngaySinh);
            if (date != null) {
                calendar.setTime(date);
            }
        } catch (ParseException e) {
            e.printStackTrace();
            // If date parsing fails, use current date
        }

        // Set up date picker dialog
        final DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                // Update the TextView with the selected date
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                tvNgaySinh.setText(sdf.format(calendar.getTime()));
            }
        };

        // When the date field is clicked, show the date picker
        tvNgaySinh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                        .setTitleText("Chọn ngày sinh")
                        .setSelection(calendar.getTimeInMillis())
                        .build();

                datePicker.addOnPositiveButtonClickListener(selection -> {
                    calendar.setTimeInMillis(selection);
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    tvNgaySinh.setText(sdf.format(calendar.getTime()));
                });

                datePicker.show(((FragmentActivity) getContext()).getSupportFragmentManager(), "DATE_PICKER");
            }
        });

        // Set up phone number input filtering
        editTextSoDienThoai.setInputType(InputType.TYPE_CLASS_PHONE);
        editTextSoDienThoai.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});

        // Create the dialog
        final AlertDialog dialog = builder.create();

        // Add click listeners for custom buttons
        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnUpdate.setOnClickListener(v -> {
            try {
                String ten = editTextHoTen.getText().toString().trim();
                String ngaySinh = tvNgaySinh.getText().toString().trim();
                String gioiTinhInput = spinnerGioiTinh.getSelectedItem().toString();
                String soDienThoai = editTextSoDienThoai.getText().toString().trim();

                // Validate inputs
                boolean isValid = true;

                if (ten.isEmpty()) {
                    editTextHoTen.setError("Vui lòng nhập họ tên");
                    isValid = false;
                }

                if (ngaySinh.isEmpty()) {
                    Snackbar.make(dialogView, "Vui lòng chọn ngày sinh", Snackbar.LENGTH_SHORT).show();
                    isValid = false;
                }

                // Validate phone number
                if (soDienThoai.isEmpty()) {
                    editTextSoDienThoai.setError("Vui lòng nhập số điện thoại");
                    isValid = false;
                } else if (!soDienThoai.matches("^0\\d{9}$")) {
                    editTextSoDienThoai.setError("Số điện thoại phải có 10 chữ số và bắt đầu bằng số 0");
                    isValid = false;
                }

                if (!isValid) {
                    return;
                }

                // Convert gender to code value
                String gioiTinhValue = gioiTinhInput.equals("Nam") ? "1" : "0";



                // Update database (you may need to adapt this to your actual DB implementation)
                // Update database
                String sql = "UPDATE NguoiDung SET hoTen = '" + ten + "', ngaySinh = '" + ngaySinh +
                        "', gioiTinh = '" + gioiTinhValue + "', soDienThoai = '" + soDienThoai +
                        "' WHERE idTaiKhoan = '" + mMainActivity.getmTk() + "'";
                db.query_noresult(sql);


                // Show success message with Snackbar
                Snackbar.make(getView(), "Cập nhật thông tin thành công", Snackbar.LENGTH_SHORT).show();

                // Reload user data
                getThongTinNguoiDung(mMainActivity.getmTk());

                // Dismiss the dialog only if all validation passes
                dialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
                Snackbar.make(getView(), "Cập nhật thông tin thất bại: " + e.getMessage(), Snackbar.LENGTH_SHORT).show();
            }
        });

        // Style and display dialog
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.rounded_dialog_background23);
        dialog.show();
    }

    private void getThongTinNguoiDung(String idTaiKhoan) {
        try {
            Cursor cursor = db.query_hasresult("SELECT * FROM TaiKhoan TK JOIN NguoiDung ND ON TK.userName = ND.idTaiKhoan WHERE idTaiKhoan = '" + idTaiKhoan + "'");
            if (cursor.getCount() != 0) {
                while (cursor.moveToNext()) {
                    Log.w("cursor=>>>>", cursor.getColumnName(0));
                    Log.w("cursor=>>>>", cursor.getColumnName(1));

                    Log.w("cursor=>>>>", cursor.getColumnName(2));

                    Log.w("cursor=>>>>", cursor.getColumnName(3));

                    Log.w("cursor=>>>>", cursor.getColumnName(4));
                    Log.w("cursor=>>>>", cursor.getColumnName(5));
                    Log.w("cursor=>>>>", cursor.getColumnName(6));


                    username = cursor.getString(0);
                    password = cursor.getString(1);
                    idND = cursor.getString(2);
                    HoTen = cursor.getString(4);
                    ngaySinh = cursor.getString(5);
                    gioiTinh = cursor.getString(6);
                    soDienThoai = cursor.getString(7);

                    // Update UI with retrieved data
                    tvTenTaiKhoan.setText(HoTen);
                    tvTitleTenTaiKhoan.setText(HoTen);
                    tvUsername.setText("@" + username.toLowerCase());
                    tvNgaySinh.setText(ngaySinh);
                    tvGioiTinh.setText(gioiTinh.equals("1") ? "Nam" : "Nữ");
                    tvSoDienThoai.setText(soDienThoai);
                    tvUserName.setText(username);
                    tvPassword.setText("••••••••••"); // Hide actual password for security
                }

                Log.d("thongtin", "User data loaded successfully for: " + username);
            } else {
                Log.d("thongtin", "No user data found for ID: " + idTaiKhoan);
                Toast.makeText(getContext(), "Không tìm thấy thông tin người dùng", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("thongtin", "Error loading user data: " + e.getMessage());
        }
    }
}