package me.trihung.learningapp2.UI;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import me.trihung.learningapp2.DB.Database;
import me.trihung.learningapp2.R;

public class ChangePasswordDialog extends Dialog {

    private final Context context;
    private final String currentPassword;
    private final String username;
    private final OnPasswordChangedListener listener;

    private TextInputLayout currentPasswordLayout, newPasswordLayout, confirmPasswordLayout;
    private TextInputEditText editCurrentPass, editNewPass, editConfirmPass;
    private MaterialButton btnCancel, btnUpdate;

    public interface OnPasswordChangedListener {
        void onPasswordChanged();
    }

    public ChangePasswordDialog(@NonNull Context context, String username, String currentPassword, OnPasswordChangedListener listener) {
        super(context);
        this.context = context;
        this.username = username;
        this.currentPassword = currentPassword;
        this.listener = listener;

        setupDialog();
    }

    private void setupDialog() {
        // Set up the dialog window
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = LayoutInflater.from(context).inflate(R.layout.change_password_dialog_layout, null);
        setContentView(view);

        // Make dialog background transparent for rounded corners
        //getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getWindow().setBackgroundDrawableResource(R.drawable.rounded_dialog_background23);

        // Set dialog width to match parent with margin
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        getWindow().setAttributes(layoutParams);

        // Initialize views
        initializeViews();

        // Set up listeners
        setupListeners();
    }

    private void initializeViews() {
        // TextInputLayouts for error handling
        currentPasswordLayout = findViewById(R.id.currentPasswordLayout);
        newPasswordLayout = findViewById(R.id.newPasswordLayout);
        confirmPasswordLayout = findViewById(R.id.confirmPasswordLayout);

        // EditTexts for input
        editCurrentPass = findViewById(R.id.editCurrentPass);
        editNewPass = findViewById(R.id.editNewPass);
        editConfirmPass = findViewById(R.id.editConfirmPass);

        // Buttons
        btnCancel = findViewById(R.id.btnCancel);
        btnUpdate = findViewById(R.id.btnUpdate);
    }

    private void setupListeners() {
        // Cancel button - dismiss dialog
        btnCancel.setOnClickListener(v -> dismiss());

        // Update button - validate and update password
        btnUpdate.setOnClickListener(v -> validateAndUpdatePassword());

        // Clear errors on text change
        editCurrentPass.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) currentPasswordLayout.setError(null);
        });

        editNewPass.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) newPasswordLayout.setError(null);
        });

        editConfirmPass.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) confirmPasswordLayout.setError(null);
        });
    }

    private void validateAndUpdatePassword() {
        // Reset errors
        currentPasswordLayout.setError(null);
        newPasswordLayout.setError(null);
        confirmPasswordLayout.setError(null);

        // Get input values
        String currentPass = editCurrentPass.getText().toString().trim();
        String newPass = editNewPass.getText().toString().trim();
        String confirmPass = editConfirmPass.getText().toString().trim();

        // Validate inputs
        boolean isValid = true;

        if (currentPass.isEmpty()) {
            currentPasswordLayout.setError("Vui lòng nhập mật khẩu hiện tại");
            isValid = false;
        } else if (!currentPass.equals(currentPassword)) {
            currentPasswordLayout.setError("Mật khẩu hiện tại không đúng");
            isValid = false;
        }

        if (newPass.isEmpty()) {
            newPasswordLayout.setError("Vui lòng nhập mật khẩu mới");
            isValid = false;
        } else if (newPass.length() < 8) {
            newPasswordLayout.setError("Mật khẩu phải có ít nhất 8 ký tự");
            isValid = false;
        } else if (!isStrongPassword(newPass)) {
            newPasswordLayout.setError("Mật khẩu không đủ mạnh");
            isValid = false;
        }

        if (confirmPass.isEmpty()) {
            confirmPasswordLayout.setError("Vui lòng xác nhận mật khẩu mới");
            isValid = false;
        } else if (!confirmPass.equals(newPass)) {
            confirmPasswordLayout.setError("Mật khẩu xác nhận không khớp");
            isValid = false;
        }

        // If all validation passes, update password
        if (isValid) {
            updatePassword(newPass);
        }
    }

    private boolean isStrongPassword(String password) {
        // Check for at least one uppercase, one lowercase, and one digit
        boolean hasUppercase = !password.equals(password.toLowerCase());
        boolean hasLowercase = !password.equals(password.toUpperCase());
        boolean hasDigit = password.matches(".*\\d.*");

        return hasUppercase && hasLowercase && hasDigit;
    }

    private void updatePassword(String newPassword) {
        try {
            // Create database instance
            Database db = new Database(context);

            // Update password in database
            String sql = "UPDATE TaiKhoan SET Password = '" + newPassword + "' WHERE Username = '" + username + "'";
            db.query_noresult(sql);

            // Show success message
            Toast.makeText(context, "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();

            // Notify listener
            if (listener != null) {
                listener.onPasswordChanged();
            }

            // Dismiss dialog
            dismiss();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Đổi mật khẩu thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}