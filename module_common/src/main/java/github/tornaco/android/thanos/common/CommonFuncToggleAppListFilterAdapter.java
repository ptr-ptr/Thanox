package github.tornaco.android.thanos.common;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.util.ArrayList;
import java.util.List;

import github.tornaco.android.thanos.core.pm.AppInfo;
import github.tornaco.android.thanos.module.common.R;
import github.tornaco.android.thanos.module.common.databinding.ItemCommonCheckableAppBinding;
import lombok.Getter;
import util.Consumer;

class CommonFuncToggleAppListFilterAdapter extends RecyclerView.Adapter<CommonFuncToggleAppListFilterAdapter.VH>
        implements Consumer<List<AppListModel>>,
        FastScrollRecyclerView.SectionedAdapter,
        FastScrollRecyclerView.MeasurableAdapter<CommonFuncToggleAppListFilterAdapter.VH> {

    private final List<AppListModel> processModels = new ArrayList<>();

    @Nullable
    private final OnAppItemSelectStateChangeListener selectStateChangeListener;
    @Nullable
    private final AppItemViewActionListener appItemViewActionListener;

    CommonFuncToggleAppListFilterAdapter(
            @Nullable OnAppItemSelectStateChangeListener selectStateChangeListener,
            @Nullable AppItemViewActionListener appItemViewActionListener) {
        this.selectStateChangeListener = selectStateChangeListener;
        this.appItemViewActionListener = appItemViewActionListener;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VH(ItemCommonCheckableAppBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        AppListModel model = processModels.get(position);
        holder.binding.setApp(model.appInfo);
        holder.binding.setIsLastOne(false);
        holder.binding.setListener(new AppItemViewActionListener() {
            @Override
            public void onAppItemClick(AppInfo appInfo) {
                holder.binding.itemSwitch.performClick();
                if (appItemViewActionListener != null) {
                    appItemViewActionListener.onAppItemClick(appInfo);
                }
            }

            @Override
            public void onAppItemSwitchStateChange(AppInfo appInfo, boolean checked) {
                appInfo.setSelected(checked);
                holder.binding.invalidateAll();
                if (selectStateChangeListener != null) {
                    selectStateChangeListener.onAppItemSelectionChanged(appInfo, checked);
                }
                if (appItemViewActionListener != null) {
                    appItemViewActionListener.onAppItemSwitchStateChange(appInfo, checked);
                }
            }
        });
        holder.binding.setBadge1(model.badge);
        holder.binding.setBadge2(model.badge2);
        holder.binding.executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return processModels.size();
    }

    @Override
    public void accept(List<AppListModel> processModels) {
        this.processModels.clear();
        this.processModels.addAll(processModels);
        notifyDataSetChanged();
    }

    @Override
    public int getViewTypeHeight(RecyclerView recyclerView, @Nullable VH viewHolder, int viewType) {
        return recyclerView.getResources().getDimensionPixelSize(R.dimen.common_list_item_height);
    }

    @NonNull
    @Override
    public String getSectionName(int position) {
        AppListModel model = processModels.get(position);
        String appName = model.appInfo.getAppLabel();
        if (appName == null
                || appName.length() < 1) {
            appName = model.appInfo.getPkgName();
        }
        if (appName == null) {
            return "*";
        }
        return String.valueOf(appName.charAt(0));
    }

    @Getter
    static final class VH extends RecyclerView.ViewHolder {
        private ItemCommonCheckableAppBinding binding;

        VH(@NonNull ItemCommonCheckableAppBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
