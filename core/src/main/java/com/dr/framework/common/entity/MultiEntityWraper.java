package com.dr.framework.common.entity;

/**
 * 关联表查询数据封装
 *
 * @param <M> 主表实体对象
 * @param <S> 从表实体对象，可以是list
 */
public class MultiEntityWraper<M, S> {
    private M main;
    private S secondary;
    private boolean ignoreIfNull = true;

    public M getMain() {
        return main;
    }

    public void setMain(M main) {
        this.main = main;
    }

    public S getSecondary() {
        return secondary;
    }

    public void setSecondary(S secondary) {
        this.secondary = secondary;
    }

    public boolean isIgnoreIfNull() {
        return ignoreIfNull;
    }

    public void setIgnoreIfNull(boolean ignoreIfNull) {
        this.ignoreIfNull = ignoreIfNull;
    }
}
