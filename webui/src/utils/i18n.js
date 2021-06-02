import Vue from 'vue'
import VueI18n from 'vue-i18n'

Vue.use(VueI18n)

// 注册i18n实例并引入语言文件，文件格式等下解析
const i18n = new VueI18n({
  locale: 'zh-CN',
  messages: {
    'zh-CN': require('@/components/lang/zh-CN.js').default,
    'en-US': require('@/components/lang/en-US.js').default
  }
})

// translate router.meta.title, be used in breadcrumb sidebar tagsview
export function generateTitle(title) {
  const hasKey = i18n.te('route.' + title)

  if (hasKey) {
    // $t :this method from vue-i18n, inject in @/lang/index.js
    return i18n.t('route.' + title)
  }
  return title
}

//切换语言
export function setLanguage(lang){
  return i18n.locale = lang
}

export function getT(value, locale=undefined, values=undefined){
  return i18n.t(value, locale, values)
}

export default i18n