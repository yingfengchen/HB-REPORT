<template>
  <div class="main">
    <a-form-model ref="form" :model="model" :rules="validatorRules" class="user-layout-login">
      <a-form-model-item required prop="username">
        <a-input v-model="model.username" size="large" :placeholder="$i18n.t('login.placeholderAccount')">
          <a-icon slot="prefix" type="user" :style="{ color: 'rgba(0,0,0,.25)' }" />
        </a-input>
      </a-form-model-item>
      <a-form-model-item required prop="password">
        <a-input v-model="model.password" size="large" type="password" autocomplete="false"
                 :placeholder="$i18n.t('login.placeholderPassword')">
          <a-icon slot="prefix" type="lock" :style="{ color: 'rgba(0,0,0,.25)' }" />
        </a-input>
      </a-form-model-item>

      <a-form-model-item>
        <a-checkbox @change="handleRememberMeChange" default-checked>{{ $t('login.rememberMe') }}</a-checkbox>
        <language-change style="float: right;" />
      </a-form-model-item>

      <a-form-item style="margin-top:24px">
        <a-button size="large" type="primary" htmlType="submit"
                  :class="{ 'login-button':true, 'reject-animation':isReject }" :loading="loginBtn"
                  @click.stop.prevent="handleSubmit" :disabled="loginBtn" ref="login_button">{{ $t('login.loginBtn') }}
        </a-button>
      </a-form-item>
    </a-form-model>

    <two-step-captcha v-if="requiredTwoStepCaptcha" :visible="stepCaptchaVisible" @success="stepCaptchaSuccess"
                      @cancel="stepCaptchaCancel"></two-step-captcha>
    <login-select-tenant ref="loginSelect" @success="loginSelectOk"></login-select-tenant>
  </div>
</template>

<script>
import { postAction, getAction } from '@/api/manage'
import Vue from 'vue'
import { ACCESS_TOKEN, ENCRYPTED_STRING } from '@/store/mutation-types'
import { mapActions } from 'vuex'
import LoginSelectTenant from './LoginSelectTenant'
import TwoStepCaptcha from '@/components/tools/TwoStepCaptcha'
import { encryption, getEncryptedString } from '@/utils/encryption/aesEncrypt'
import { timeFix } from '@/utils/util'
import LanguageChange from '@comp/tools/LanguageChange'
import { getT } from '@/utils/i18n'

export default {
  components: {
    LanguageChange,
    LoginSelectTenant,
    TwoStepCaptcha
  },
  data() {
    return {
      model: {
        username: '',
        password: ''
      },
      loginType: 0,
      validatorRules: {
        username: [
          { required: true, message: '请输入用户名!' },
          { validator: this.handleUsernameOrEmail }
        ],
        password: [{
          required: true, message: '请输入密码!', validator: 'click'
        }],
        mobile: [
          { required: true, message: '请输入手机号码!' },
          { validator: this.validateMobile }
        ]
      },
      requestCodeSuccess: false,
      randCodeImage: '',
      currdatetime: '',
      loginBtn: false,
      requiredTwoStepCaptcha: false,
      stepCaptchaVisible: false,
      //手机号登录用
      state: {
        time: 60,
        smsSendBtn: false
      },
      encryptedString: {
        key: '',
        iv: ''
      },
      isReject: false
    }
  },
  created() {
    this.currdatetime = new Date().getTime()
    this.model.rememberMe = true
    Vue.ls.remove(ACCESS_TOKEN)
    this.getRouterData()
    this.handleChangeCheckCode()
  },
  methods: {
    ...mapActions(['Login', 'Logout', 'PhoneLogin']),
    handleUsernameOrEmail(rule, value, callback) {
      const regex = /^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+((\.[a-zA-Z0-9_-]{2,3}){1,2})$/
      if (regex.test(value)) {
        this.loginType = 0
      } else {
        this.loginType = 1
      }
      callback()
    },
    /**刷新验证码*/
    handleChangeCheckCode() {
      this.currdatetime = new Date().getTime()
      getAction(`/sys/randomImage/${this.currdatetime}`).then(res => {
        if (res.success) {
          this.randCodeImage = res.result
          this.requestCodeSuccess = true
        } else {
          this.$message.error(res.message)
          this.requestCodeSuccess = false
        }
      }).catch(() => {
        this.requestCodeSuccess = false
      })
    },
    /**跳转到登录页面的参数-账号获取*/
    getRouterData() {
      this.$nextTick(() => {
        let temp = this.$route.params.username || this.$route.query.username || ''
        if (temp) {
          this.model['username'] = temp
        }
      })
    },
    handleRememberMeChange(e) {
      this.model.rememberMe = e.target.checked
    },
    //登录
    handleSubmit() {
      let that = this
      let loginParams = {}
      that.loginBtn = true
      // 使用账户密码登录
      this.loginByUsername()
    },
    /**
     * 验证字段
     * @param arr
     * @param callback
     */
    validateFields(arr, callback) {
      let promiseArray = []
      for (let item of arr) {
        let p = new Promise((resolve, reject) => {
          this.$refs['form'].validateField(item, (err) => {
            if (!err) {
              resolve()
            } else {
              reject(err)
            }
          })
        })
        promiseArray.push(p)
      }
      Promise.all(promiseArray).then(() => {
        callback()
      }).catch(err => {
        callback(err)
      })
    },
    //账号密码登录
    loginByUsername() {
      this.isReject = false
      this.validateFields(['username', 'password'], (err) => {
        if (!err) {
          let loginParams = {
            username: this.model.username,
            password: this.model.password,
            remember_me: this.model.rememberMe,
            checkKey: this.currdatetime
          }
          console.log('登录参数', loginParams)
          this.Login(loginParams).then((res) => {
            this.$refs.loginSelect.show(res.result)
          }).catch((err) => {
            this.requestFailed(err)
            this.isReject = true
          })
        } else {
          this.loginBtn = false
          this.isReject = true
        }
      })
    },
    //手机号码登录
    loginByPhone() {
      this.validateFields(['mobile', 'captcha'], (err) => {
        if (!err) {
          let loginParams = {
            mobile: this.model.mobile,
            captcha: this.model.captcha,
            remember_me: this.model.rememberMe
          }
          console.log('登录参数', loginParams)
          this.PhoneLogin(loginParams).then((res) => {
            console.log(res.result)
            this.$refs.loginSelect.show(res.result)
          }).catch((err) => {
            this.requestFailed(err)
          })
        } else {
          this.loginBtn = false
        }
      })
    },
    //登录后台失败
    requestFailed(err) {
      let description = ((err.response || {}).data || {}).message || err.message || '请求出现错误，请稍后再试'
      this.$notification['error']({
        message: '登录失败',
        description: description,
        duration: 4
      })
      //密码错误后更新验证码
      if (description.indexOf('密码错误') > 0) {
        this.handleChangeCheckCode()
      }
      this.loginBtn = false
    },
    loginSelectOk() {
      this.loginSuccess()
    },
    //登录成功
    loginSuccess() {
      this.$router.push({ path: '/dashboard/analysis' }).catch(() => {
        console.log('登录跳转首页出错,这个错误从哪里来的')
      })
      this.$notification.success({
        message: getT('index.welcome'),
        description: `${timeFix()}，` + getT('index.welcomebak')
      })
    },
    validateMobile(rule, value, callback) {
      if (!value || new RegExp(/^1([38][0-9]|4[579]|5[0-3,5-9]|6[6]|7[0135678]|9[89])\d{8}$/).test(value)) {
        callback()
      } else {
        callback('您的手机号码格式不正确!')
      }
    },
    //获取验证码
    getCaptcha(e) {
      e.preventDefault()
      let that = this
      that.validateFields(['mobile'], (err) => {
          if (!err) {
            that.state.smsSendBtn = true
            let interval = window.setInterval(() => {
              if (that.state.time-- <= 0) {
                that.state.time = 60
                that.state.smsSendBtn = false
                window.clearInterval(interval)
              }
            }, 1000)

            const hide = that.$message.loading('验证码发送中..', 0)
            let smsParams = {}
            smsParams.mobile = that.model.mobile
            smsParams.smsmode = '0'
            postAction('/sys/sms', smsParams)
              .then(res => {
                if (!res.success) {
                  setTimeout(hide, 0)
                  that.cmsFailed(res.message)
                }
                console.log(res)
                setTimeout(hide, 500)
              })
              .catch(err => {
                setTimeout(hide, 1)
                clearInterval(interval)
                that.state.time = 60
                that.state.smsSendBtn = false
                that.requestFailed(err)
              })
          }
        }
      )
    },
    cmsFailed(err) {
      this.$notification['error']({
        message: '登录失败',
        description: err,
        duration: 4
      })
    },
    stepCaptchaSuccess() {
      this.loginSuccess()
    },
    stepCaptchaCancel() {
      this.Logout().then(() => {
        this.loginBtn = false
        this.stepCaptchaVisible = false
      })
    },
    //获取密码加密规则
    getEncrypte() {
      var encryptedString = Vue.ls.get(ENCRYPTED_STRING)
      if (encryptedString == null) {
        getEncryptedString().then((data) => {
          this.encryptedString = data
        })
      } else {
        this.encryptedString = encryptedString
      }
    }

  }

}
</script>
<style lang="less" scoped>
.user-layout-login {
  margin-top: 25%;

  label {
    font-size: 14px;
  }

  .getCaptcha {
    display: block;
    width: 100%;
    height: 40px;
  }

  .forge-password {
    font-size: 14px;
  }

  button.login-button {
    padding: 0 15px;
    font-size: 16px;
    height: 40px;
    width: 100%;
  }

  .reject-animation {
    -webkit-animation: shake-horizontal 0.8s cubic-bezier(0.455, 0.030, 0.515, 0.955) both;
    animation: shake-horizontal 0.8s cubic-bezier(0.455, 0.030, 0.515, 0.955) both;
  }

  .user-login-other {
    text-align: left;
    margin-top: 24px;
    line-height: 22px;

    .item-icon {
      font-size: 24px;
      color: rgba(0, 0, 0, .2);
      margin-left: 16px;
      vertical-align: middle;
      cursor: pointer;
      transition: color .3s;

      &:hover {
        color: #1890ff;
      }
    }

    .register {
      float: right;
    }
  }
}
</style>
<style>
.valid-error .ant-select-selection__placeholder {
  color: #f5222d;
}
</style>