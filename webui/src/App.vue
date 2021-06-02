<template>
  <a-config-provider :locale="locale">
    <div id="app">
      <router-view />
    </div>
  </a-config-provider>
</template>
<script>
import zhCN from 'ant-design-vue/lib/locale-provider/zh_CN'
import enUS from 'ant-design-vue/lib/locale-provider/en_US'
import enquireScreen from '@/utils/device'

export default {
  data() {
    return {
      locale: this.$i18n.locale === 'zh-CN' ? zhCN : enUS
    }
  },
  created() {
    let that = this
    enquireScreen(deviceType => {
      // tablet
      if (deviceType === 0) {
        that.$store.commit('TOGGLE_DEVICE', 'mobile')
        that.$store.dispatch('setSidebar', false)
      }
      // mobile
      else if (deviceType === 1) {
        that.$store.commit('TOGGLE_DEVICE', 'mobile')
        that.$store.dispatch('setSidebar', false)
      } else {
        that.$store.commit('TOGGLE_DEVICE', 'desktop')
        that.$store.dispatch('setSidebar', true)
      }
    })
    this.$bus.$on('language_change', (args) => {
      if (args['lang'] === 'zh-CN') {
        that.locale = zhCN
      } else if (args['lang'] === 'en-US') {
        that.locale = enUS
      }
    })
  }
}
</script>
<style>
#app {
  height: 100%;
}
</style>