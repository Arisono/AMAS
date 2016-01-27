// Generated code from Butter Knife. Do not modify!
package com.ASMS.activity;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class GroupContactsActivity$$ViewBinder<T extends com.ASMS.activity.GroupContactsActivity> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131230811, "field 'lv_group' and method 'item_onClik'");
    target.lv_group = finder.castView(view, 2131230811, "field 'lv_group'");
    ((android.widget.AdapterView<?>) view).setOnItemClickListener(
      new android.widget.AdapterView.OnItemClickListener() {
        @Override public void onItemClick(
          android.widget.AdapterView<?> p0,
          android.view.View p1,
          int p2,
          long p3
        ) {
          target.item_onClik(p0, p1, p2);
        }
      });
    view = finder.findRequiredView(source, 2131230831, "field 'tv_title'");
    target.tv_title = finder.castView(view, 2131230831, "field 'tv_title'");
    view = finder.findRequiredView(source, 2131230830, "method 'iv_back'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.iv_back();
        }
      });
  }

  @Override public void unbind(T target) {
    target.lv_group = null;
    target.tv_title = null;
  }
}
