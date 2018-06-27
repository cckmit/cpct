package com.zjtelcom.cpct.common;

/**
 * <p>
 * <b>版权：</b>Copyright (c) 2015 .<br>
 * <b>工程：</b>TBPO<br>
 * <b>文件：</b>DacheConfig.java<br>
 * <b>创建时间：</b>2015-6-9 上午9:16:15<br>
 * <p>
 * <b>缓存配置.</b><br>
 * </p>
 *
 * @author XIX
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
public class DacheConfig {
    public String name;
    public int maxElementsInMemory;
    public boolean overflowToDisk;
    public boolean eternal;
    public long timeToLiveSeconds;
    public long timeToIdleSeconds;
}
