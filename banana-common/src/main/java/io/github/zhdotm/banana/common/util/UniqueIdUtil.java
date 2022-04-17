package io.github.zhdotm.banana.common.util;

import cn.hutool.core.util.IdUtil;

/**
 * 唯一ID生成工具
 *
 * @author zhihao.mao
 */

public class UniqueIdUtil {

    public static String getNextId(String prefix) {

        return prefix + ":" + IdUtil.getSnowflake().nextIdStr();
    }

}
