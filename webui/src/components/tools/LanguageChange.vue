<template>
  <a-dropdown>
    <a-menu slot="overlay" @click="handleMenuClick">
      <a-menu-item key="zh-CN"> {{ $t('lang', 'zh-CN') }}</a-menu-item>
      <a-menu-item key="en-US"> {{ $t('lang', 'en-US') }}</a-menu-item>
    </a-menu>
    <span class="language-change">
      <i style="font-size: 16px; padding: 4px" class="yicon-common yiconlanguage" />
    </span>
  </a-dropdown>
</template>

<script>
import Vue from 'vue'

export default {
  name: 'LanguageChange',
  methods: {
    handleMenuClick(data) {
      this.$i18n.locale = data['key']
      Vue.ls.set('language', data['key'])
      this.$bus.$emit('language_change', { lang: data['key'] })
    }
  }
}
</script>

<style lang="less" scoped>
.language-change-wrapper {
  top: 50px !important;
}

.language-change {
  width: 50px;
  display: inline-block;
  transition: all 0.3s;
  text-align: center;

  span {
    vertical-align: initial;
  }
}

.language-change:hover {
  background: #f1f1f1;
}
</style>