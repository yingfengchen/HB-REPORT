/** init domain config */
import './config'

import Vue from 'vue'
import App from './App.vue'
import Storage from 'vue-ls'
import router from './router'
import store from './store/'
import { VueAxios } from '@/utils/request'

require('@jeecg/antd-online-mini')
require('@jeecg/antd-online-mini/dist/OnlineForm.css')

import Antd, { version } from 'ant-design-vue'

console.log('ant-design-vue version:', version)

import Viser from 'viser-vue'
import 'ant-design-vue/dist/antd.less'  // or 'ant-design-vue/dist/antd.less'

import 'xe-utils'
import 'vxe-table/lib/style.css'
import VXETable from 'vxe-table'

import '@/permission' // permission control
import '@/utils/filter' // base filter
import Print from 'vue-print-nb-jeecg'
import preview from 'vue-photo-preview'
import 'vue-photo-preview/dist/skin.css'
import SSO from '@/cas/sso.js'
import {
  ACCESS_TOKEN,
  DEFAULT_COLOR,
  DEFAULT_THEME,
  DEFAULT_LAYOUT_MODE,
  DEFAULT_COLOR_WEAK,
  SIDEBAR_TYPE,
  DEFAULT_FIXED_HEADER,
  DEFAULT_FIXED_HEADER_HIDDEN,
  DEFAULT_FIXED_SIDEMENU,
  DEFAULT_CONTENT_WIDTH_TYPE,
  DEFAULT_MULTI_PAGE
} from '@/store/mutation-types'
import config from '@/defaultSettings'

import JDictSelectTag from './components/dict/index.js'
import hasPermission from '@/utils/hasPermission'
import vueBus from '@/utils/vueBus'
import JeecgComponents from '@/components/jeecg/index'
import '@/assets/less/JAreaLinkage.less'
import VueAreaLinkage from 'vue-area-linkage'
//表单验证
import { rules } from '@/utils/rules'
//按需引入DV
import { decoration2, decoration3, decoration6, decoration8, borderBox11, scrollBoard } from '@jiaminghi/data-view'
//国际化
import i18n, { setLanguage } from '@/utils/i18n'

Vue.prototype.rules = rules
Vue.config.productionTip = false
Vue.use(Storage, config.storageOptions)
Vue.use(Antd)
Vue.use(VueAxios, router)
Vue.use(Viser)
Vue.use(hasPermission)
Vue.use(JDictSelectTag)
Vue.use(Print)
Vue.use(preview)
Vue.use(vueBus)
Vue.use(JeecgComponents)
Vue.use(VueAreaLinkage)
Vue.use(VXETable)
Vue.use(decoration2)
Vue.use(decoration3)
Vue.use(decoration6)
Vue.use(decoration8)
Vue.use(borderBox11)
Vue.use(scrollBoard)

SSO.init(() => {
  main()
})

function isFullScreen() {
  return  !! (
    document.fullscreen ||
    document.mozFullScreen ||
    document.webkitIsFullScreen ||
    document.webkitFullScreen ||
    document.msFullScreen
  );
}

function main() {
  setLanguage(Vue.ls.get('language', 'zh-CN'))
  window.onload = window.onresize = function() {
    if (document.body.clientWidth < 1600) {
      store.commit('SET_BODY_HEIGHT', document.body.clientHeight / 0.8)
    } else {
      store.commit('SET_BODY_HEIGHT', document.body.clientHeight)
    }

    store.commit('SET_FULLSCREEN_MODE', isFullScreen())
  }
  new Vue({
    router,
    store,
    i18n,
    mounted() {
      store.commit('SET_SIDEBAR_TYPE', Vue.ls.get(SIDEBAR_TYPE, true))
      store.commit('TOGGLE_THEME', Vue.ls.get(DEFAULT_THEME, config.navTheme))
      store.commit('TOGGLE_LAYOUT_MODE', Vue.ls.get(DEFAULT_LAYOUT_MODE, config.layout))
      store.commit('TOGGLE_FIXED_HEADER', Vue.ls.get(DEFAULT_FIXED_HEADER, config.fixedHeader))
      store.commit('TOGGLE_FIXED_SIDERBAR', Vue.ls.get(DEFAULT_FIXED_SIDEMENU, config.fixSiderbar))
      store.commit('TOGGLE_CONTENT_WIDTH', Vue.ls.get(DEFAULT_CONTENT_WIDTH_TYPE, config.contentWidth))
      store.commit('TOGGLE_FIXED_HEADER_HIDDEN', Vue.ls.get(DEFAULT_FIXED_HEADER_HIDDEN, config.autoHideHeader))
      store.commit('TOGGLE_WEAK', Vue.ls.get(DEFAULT_COLOR_WEAK, config.colorWeak))
      store.commit('TOGGLE_COLOR', Vue.ls.get(DEFAULT_COLOR, config.primaryColor))
      store.commit('SET_TOKEN', Vue.ls.get(ACCESS_TOKEN))
      store.commit('SET_MULTI_PAGE', Vue.ls.get(DEFAULT_MULTI_PAGE, config.multipage))
    },
    render: h => h(App)
  }).$mount('#app')
}