package model;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * 現在日時を取得するユーティリティ。
 * デフォルトではシステムのデフォルトタイムゾーンを使用して LocalDateTime を返す。
 */
public class TodaysDateTime {

    // デフォルト：システムデフォルトゾーンの LocalDateTime を返す
    public static LocalDateTime getNow() {
        return LocalDateTime.now();
    }

    // 明示的に ZoneId を指定して LocalDateTime を取得するオーバーロード
    public static LocalDateTime getNow(ZoneId zone) {
        ZonedDateTime zdt = ZonedDateTime.now(zone);
        return zdt.toLocalDateTime();
    }

    // UTC の LocalDateTime を取得する便宜メソッド
    public static LocalDateTime getNowUtc() {
        return getNow(ZoneId.of("UTC"));
    }

    // 必要ならフォーマット済み文字列を返すメソッドも追加可能
}