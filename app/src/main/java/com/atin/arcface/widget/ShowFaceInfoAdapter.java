package com.atin.arcface.widget;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.atin.arcface.R;
import com.atin.arcface.activity.RegisterAndRecognizeDualActivity;
import com.atin.arcface.common.AccessBussiness;
import com.atin.arcface.common.CompanyConstantParam;
import com.atin.arcface.common.Constants;
import com.atin.arcface.common.EmitSound;
import com.atin.arcface.common.ErrorCode;
import com.atin.arcface.common.MachineName;
import com.atin.arcface.faceserver.CompareResult;
import com.atin.arcface.service.SingletonObject;
import com.atin.arcface.util.BaseUtil;
import com.atin.arcface.util.LanguageUtils;
import com.atin.arcface.util.MachineFunctionUtils;
import com.atin.arcface.util.StringUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.Date;
import java.util.List;
import static android.content.Context.MODE_PRIVATE;

public class ShowFaceInfoAdapter extends RecyclerView.Adapter<ShowFaceInfoAdapter.CompareResultHolder> {
    private static final String TAG = "ShowFaceInfoAdapter";
    private List<CompareResult> lsCompareResult;
    private LayoutInflater inflater;
    private View itemView;
    private Context context;
    private SharedPreferences pref;
    private int avatarWidth = 200;
    private int avatarHeigh = 200;

    public ShowFaceInfoAdapter(Context context, List<CompareResult> lsCompareResult) {
        inflater = LayoutInflater.from(context);
        this.lsCompareResult = lsCompareResult;
        this.context = context;
        initValue();
    }

    public  void initValue(){
        pref = context.getSharedPreferences(Constants.SHARE_PREFERENCE, MODE_PRIVATE);
    }

    @NonNull
    @Override
    public CompareResultHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        itemView = inflater.inflate(R.layout.item_head, null, false);
        CompareResultHolder compareResultHolder = new CompareResultHolder(itemView);
        compareResultHolder.txtFullName = itemView.findViewById(R.id.txtName);
        compareResultHolder.txtPosition = itemView.findViewById(R.id.txtPosition);
        compareResultHolder.txtJobduties = itemView.findViewById(R.id.txtJobduties);
        compareResultHolder.txtNotification = itemView.findViewById(R.id.txtNotificationContent);
        compareResultHolder.imgAvatar = itemView.findViewById(R.id.imgAvatar);
        compareResultHolder.vResult = itemView.findViewById(R.id.fixSizeDialog);
        compareResultHolder.btnConfirm = itemView.findViewById(R.id.btnOk);
        compareResultHolder.btnCancel = itemView.findViewById(R.id.btnCancel);
        return compareResultHolder;
    }

    public String getURLForResource(int resourceId) {
        return Uri.parse("android.resource://" + R.class.getPackage().getName() + "/" + resourceId).toString();
    }

    @Override
    public void onBindViewHolder(@NonNull CompareResultHolder holder, int position) {
        if (lsCompareResult == null) {
            return;
        }

        try{
            setSizeResult(holder);
            normalStyle(holder);

            CompareResult compareResult = lsCompareResult.get(0);

            if (compareResult.getMethod() == Constants.AccessType.QRCODE_RECOGNIZE) {
                Glide.with(holder.imgAvatar)
                        .load(Base64.decode(compareResult.getFaceCapture(), Base64.DEFAULT))
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .override(avatarWidth, avatarHeigh)
                        .into(holder.imgAvatar);
            } else {
                Glide.with(holder.imgAvatar)
                        .load(compareResult.getFacePath())
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .override(avatarWidth, avatarHeigh)
                        .into(holder.imgAvatar);
            }

            holder.txtFullName.setText(LanguageUtils.getString(R.string.label_user_name) + " " + StringUtils.nvl(compareResult.getFullName()));

            //Mặc định hiển thị mã nhân sự/tắt option sẽ hiện chức danh
            holder.txtPosition.setText(LanguageUtils.getString(R.string.label_position_name) + " " + StringUtils.nvl(compareResult.getPosition()));

            //Thay công việc bằng phòng ban
            holder.txtJobduties.setText(LanguageUtils.getString(R.string.label_department_name) + " " + StringUtils.nvl(compareResult.getJobDuties()));

            String summaryCode = compareResult.getSummaryCode();
            String detailCode = compareResult.getDetailCode();
            switch (summaryCode) {
                case ErrorCode.COMMON_HIGHT_TEMPERATURE:
                    switch (detailCode) {
                        case ErrorCode.COMMON_FACE_NOT_FOUND:
                            Glide.with(context)
                                    .asBitmap()
                                    .load(R.drawable.ico_shibieshibai)
                                    .override(avatarWidth, avatarHeigh)
                                    .into(holder.imgAvatar);
                            errorStyle(holder);
                            holder.txtNotification.setText(LanguageUtils.getString(R.string.label_hight_body_temperature));
                            break;

                        default:
                            warningStyle(holder);
                            holder.txtNotification.setText(LanguageUtils.getString(R.string.label_hight_body_temperature));
                            break;
                    }
                    break;

                case ErrorCode.COMMON_NO_MASK:
                    switch (detailCode) {
                        case ErrorCode.COMMON_FACE_NOT_FOUND:
                            Glide.with(context)
                                    .asBitmap()
                                    .load(R.drawable.ico_shibieshibai)
                                    .override(avatarWidth, avatarHeigh)
                                    .into(holder.imgAvatar);
                            warningStyle(holder);
                            holder.txtNotification.setText(LanguageUtils.getString(R.string.label_not_wear_mask));
                            break;

                        case ErrorCode.CHECKIN_NOT_ACCESS:
                            warningStyle(holder);
                            holder.txtNotification.setText(LanguageUtils.getString(R.string.label_not_access_permission));
                            break;

                        case ErrorCode.CHECKOUT_NOT_ACCESS:
                            warningStyle(holder);
                            holder.txtNotification.setText(LanguageUtils.getString(R.string.label_not_access_permission));
                            break;

                        case ErrorCode.TIMEKEEPING_NOT_ACCESS:
                            warningStyle(holder);
                            holder.txtNotification.setText(LanguageUtils.getString(R.string.label_not_access_permission));
                            break;
                    }
                    break;

                case ErrorCode.COMMON_FACE_NOT_FOUND:
                    Glide.with(context)
                            .asBitmap()
                            .load(R.drawable.ico_shibieshibai)
                            .override(avatarWidth, avatarHeigh)
                            .into(holder.imgAvatar);
                    errorStyle(holder);
                    holder.txtNotification.setText(LanguageUtils.getString(R.string.label_person_not_found));
                    break;

                case ErrorCode.COMMON_ACCESS_OUT_OF_SERVICE_TIME:
                    warningStyle(holder);
                    holder.txtNotification.setText(LanguageUtils.getString(R.string.label_out_off_time));
                    break;

                case ErrorCode.COMMON_NOT_ACCESS:
                    warningStyle(holder);
                    holder.txtNotification.setText(LanguageUtils.getString(R.string.label_not_access_permission));
                    break;

                case ErrorCode.COMMON_EXPIRED:
                    warningStyle(holder);
                    holder.txtNotification.setText(LanguageUtils.getString(R.string.label_access_time_expired));
                    break;

                case ErrorCode.COMMON_MACHINE_NOT_DEFINE:
                    errorStyle(holder);
                    holder.txtNotification.setText(LanguageUtils.getString(R.string.label_device_not_define));
                    break;

                case ErrorCode.COMMON_CONFIG_ERROR:
                    errorStyle(holder);
                    holder.txtNotification.setText(LanguageUtils.getString(R.string.label_missing_access_permission));
                    break;

                case ErrorCode.COMMON_ACCOUNT_SUPPEND:
                    errorStyle(holder);
                    holder.txtNotification.setText(LanguageUtils.getString(R.string.message_account_suppend));
                    break;

                case ErrorCode.COMMON_ACCESS_VALID:
                    normalStyle(holder);

                    int machineFunction = MachineFunctionUtils.getMachineFunction().getFunctionValue();
                    if(machineFunction == Constants.CANTEEN){
                        holder.txtNotification.setVisibility(View.VISIBLE);
                        holder.txtNotification.setText("Bạn có muốn đồng ý sử dụng bữa ăn này?");
                        holder.btnConfirm.setVisibility(View.VISIBLE);
                        holder.btnCancel.setVisibility(View.VISIBLE);
                        SingletonObject.getInstance(context).getMainActivity().updateWaitDialogTime(20000);
                    }
                    break;

                case ErrorCode.COMMON_USED_UP_TURN_ACCESS:
                    warningStyle(holder);
                    holder.txtNotification.setText(compareResult.getNote());
                    break;

                case ErrorCode.COMMON_CANTEEN_USED_UP_TURN_ACCESS_DAY:
                    warningStyle(holder);
                    holder.txtNotification.setText(compareResult.getNote());
                    break;

                case ErrorCode.COMMON_CANTEEN_USED_UP_TURN_ACCESS_MONTH:
                    warningStyle(holder);
                    holder.txtNotification.setText(compareResult.getNote());
                    break;

                default:
                    normalStyle(holder);
                    break;
            }

            // Thiết lập sự kiện click cho button
            holder.btnConfirm.setOnClickListener(v -> {
                try {
                    EmitSound.openSoundSpecial(context, ErrorCode.TING_NOTIFICATION);
                    SingletonObject.getInstance(context).getMainActivity().confirmCanteen(compareResult);
                    lsCompareResult.clear();
                    notifyDataSetChanged();
                } catch (Exception ex) {
                    Log.e("Error getTicketDetail", ex.getMessage());
                }
            });

            holder.btnCancel.setOnClickListener(v -> {
                try {
                    EmitSound.openSoundSpecial(context, ErrorCode.BUTTON_CLICK);
                    lsCompareResult.clear();
                    notifyDataSetChanged();
                } catch (Exception ex) {
                    Log.e("Error click close button", ex.getMessage());
                }
            });

        }catch (Exception ex){
            Log.e(TAG, ex.getMessage());
        }
    }

    private void normalStyle(CompareResultHolder holder) {
        holder.txtFullName.setVisibility(View.VISIBLE);
        holder.txtPosition.setVisibility(View.VISIBLE);
        holder.txtJobduties.setVisibility(View.VISIBLE);
        holder.txtNotification.setVisibility(View.INVISIBLE);
        holder.txtNotification.setText("");
        holder.txtNotification.setTextColor(Color.GREEN);
        holder.txtJobduties.setVisibility(View.VISIBLE);
        holder.btnConfirm.setVisibility(View.GONE);
        holder.btnCancel.setVisibility(View.GONE);
    }

    private void errorStyle(CompareResultHolder holder) {
        holder.txtFullName.setVisibility(View.INVISIBLE);
        holder.txtPosition.setVisibility(View.INVISIBLE);
        holder.txtJobduties.setVisibility(View.INVISIBLE);
        holder.txtNotification.setVisibility(View.VISIBLE);
        holder.txtNotification.setTextColor(Color.RED);
        holder.btnConfirm.setVisibility(View.GONE);
        holder.btnCancel.setVisibility(View.GONE);
    }

    private void warningStyle(CompareResultHolder holder) {
        holder.txtNotification.setVisibility(View.VISIBLE);
        holder.txtNotification.setTextColor(Color.RED);
        holder.btnConfirm.setVisibility(View.GONE);
        holder.btnCancel.setVisibility(View.GONE);
    }

    private void setSizeResult(CompareResultHolder holder) {
        int height = 260;
        int contentSize = 20;
        int notificationSize = 18;
        avatarWidth = 200;
        avatarHeigh = 200;

        switch (Build.MODEL) {
            case MachineName.RAKINDA_F3:
                height = 400;
                break;

            case MachineName.RAKINDA_F6:
                height = 460;
                contentSize = 24;
                notificationSize = 28;
                break;

            case MachineName.RAKINDA_A80M:
                height = 500;
                contentSize = 24;
                notificationSize = 28;
                break;

            case MachineName.TELPO_TPS950:
                height = 300;
                contentSize = 14;
                notificationSize = 20;
                break;

            case MachineName.TELPO_F8:
                height = 340;
                contentSize = 18;
                notificationSize = 20;
                break;

            default:
                break;
        }

        ViewGroup.LayoutParams layoutParams = holder.vResult.getLayoutParams();
        layoutParams.height = BaseUtil.dpToPx(height, context);
        holder.vResult.setLayoutParams(layoutParams);

        holder.txtFullName.setTextSize(TypedValue.COMPLEX_UNIT_SP, contentSize);
        holder.txtPosition.setTextSize(TypedValue.COMPLEX_UNIT_SP, contentSize);
        holder.txtJobduties.setTextSize(TypedValue.COMPLEX_UNIT_SP, contentSize);
        holder.txtNotification.setTextSize(TypedValue.COMPLEX_UNIT_SP, notificationSize);
        holder.btnConfirm.setTextSize(TypedValue.COMPLEX_UNIT_SP, contentSize);
        holder.btnCancel.setTextSize(TypedValue.COMPLEX_UNIT_SP, contentSize);

        int avatarWidthConvert = BaseUtil.dpToPx(avatarWidth, context);
        LinearLayout.LayoutParams layoutParamsAvatar = new LinearLayout.LayoutParams(avatarWidthConvert, avatarHeigh);
        holder.imgAvatar.setLayoutParams(layoutParamsAvatar);
    }

    @Override
    public int getItemCount() {
        return lsCompareResult == null ? 0 : lsCompareResult.size();
    }

    class CompareResultHolder extends RecyclerView.ViewHolder {
        TextView txtFullName, txtPosition, txtJobduties, txtNotification;
        ImageView imgAvatar;
        Button btnConfirm, btnCancel;
        View vResult;

        CompareResultHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
