package github.tornaco.android.thanos.process;

import github.tornaco.android.thanos.core.pm.AppInfo;
import github.tornaco.android.thanos.core.process.ProcessRecord;
import github.tornaco.java.common.util.PinyinComparatorUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@AllArgsConstructor
@Getter
@ToString
public class ProcessModel implements Comparable<ProcessModel> {
    private List<ProcessRecord> processRecord;
    private AppInfo appInfo;
    private long size;
    private String sizeStr;

    @Override
    public int compareTo(ProcessModel processModel) {
        return PinyinComparatorUtils.compare(
                this.appInfo.getAppLabel(),
                processModel.appInfo.getAppLabel());
    }
}
