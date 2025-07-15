package gitlet;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.TimeZone;

public class MyTest {
    public static void main(String[] args) {
        // 创建 Epoch 时间（1970-01-01T00:00:00Z）
        Instant epoch = Instant.ofEpochMilli(0);

        // 转为 ZonedDateTime 以便格式化
        ZonedDateTime zdt = epoch.atZone(ZoneOffset.UTC);

        // 自定义格式：00:00:00 UTC, Thursday, 1 January 1970
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss z, EEEE, d MMMM yyyy")
                .withZone(ZoneOffset.UTC);

        // 格式化输出
        String formatted = formatter.format(zdt);
        System.out.println(formatted);
    }
}
