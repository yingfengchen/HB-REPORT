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
//扩展图标
import '@/components/iconfont/common.less'
import '@/components/iconfont/iconfont.js'

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
    this.$bus.$on('fullscreen', () => {
      if (that.$store.getters.fullScreen) {
        that.fullScreenExit()
      } else {
        that.fullScreen()
      }
    })
  },
  methods: {
    fullScreen() {
      const el = document.getElementById('app')
      //HTML W3C 提议
      if (el.requestFullScreen) {
        el.requestFullScreen()
      }
      //IE11
      else if (el.msRequestFullscreen) {
        el.msRequestFullscreen()
      }
      //webkit 内核
      else if (el.webkitRequestFullScreen) {
        el.webkitRequestFullScreen()
      }
      // Firefox (works in nightly)
      else if (el.mozRequestFullScreen) {
        el.mozRequestFullScreen()
      }
      this.$store.dispatch('SetFullScreen', true)
    },
    fullScreenExit() {
      const el = document.getElementById('app')
      //HTML W3C 提议
      if (el.requestFullScreen) {
        el.exitFullScreen()
      }
      //IE11
      else if (el.msRequestFullscreen) {
        document.msExitFullscreen()
      }
      //webkit 内核
      else if (el.webkitRequestFullScreen) {
        document.webkitCancelFullScreen()
      }
      // Firefox (works in nightly)
      else if (el.mozRequestFullScreen) {
        document.mozCancelFullScreen()
      }
      this.$store.dispatch('SetFullScreen', false)
    }
  }
}
</script>
<style>
#app {
  height: 100%;
}
</style>