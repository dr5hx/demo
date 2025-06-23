package com.dr5hx.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * MySourceAnnotation
 * Desc:
 * Date:2025/6/20 14:58
 * Author:zhouchang
 * Email:zhouchang@asiainfo.com
 */
@Retention(RetentionPolicy.SOURCE)
public @interface MySourceAnnotation {
    String value();
}