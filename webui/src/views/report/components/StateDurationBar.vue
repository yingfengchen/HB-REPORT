<template>
  <a-popover title="状态比例">
    <template slot="content">
      <div class="label-content"><label style="font-weight: bold">RUN: </label><label>{{ run + 'H (' + ((total>0?run*100/total:0).toFixed(2)) + '%)' }}</label></div>
      <div class="label-content"><label style="font-weight: bold">IDLE: </label><label>{{ idle + 'H (' + ((total>0?idle*100/total:0).toFixed(2)) + '%)' }}</label></div>
      <div class="label-content"><label style="font-weight: bold">PM: </label><label>{{ pm + 'H (' + ((total>0?pm*100/total:0).toFixed(2)) + '%)' }}</label></div>
      <div class="label-content"><label style="font-weight: bold">STOP: </label><label>{{ stop + 'H (' + ((total>0?stop*100/total:0).toFixed(2)) + '%)' }}</label></div>
      <div class="label-content"><label style="font-weight: bold">TROUBLE: </label><label>{{ trouble + 'H (' + ((total>0?trouble*100/total:0).toFixed(2)) + '%)' }}</label></div>
    </template>
    <div class="state-bar">
      <div v-if="run > 0" class="state-run-block" :style="{width: (((run || 0)*100/(total || 1)).toFixed(2)) + '%'}"></div>
      <div v-if="idle > 0"  class="state-idle-block" :style="{width: (((idle || 0)*100/(total || 1)).toFixed(2)) + '%'}"></div>
      <div v-if="pm > 0"  class="state-pm-block" :style="{width: (((pm || 0)*100/(total || 1)).toFixed(2)) + '%'}"></div>
      <div v-if="stop > 0"  class="state-stop-block" :style="{width: (((stop || 0)*100/(total || 1)).toFixed(2)) + '%'}"></div>
      <div v-if="trouble > 0"  class="state-trouble-block" :style="{width: (((trouble || 0)*100/(total || 1)).toFixed(2)) + '%'}"></div>
    </div>
  </a-popover>
</template>

<script>
export default {
  name: 'StateDurationBar',
  props: ['run', 'idle', 'stop', 'pm', 'trouble', 'total']
}
</script>

<style lang="less" scoped>
.state-bar {
  width: 100%;
  height: 10px;
  border-radius: 10px;
  background: #bcbcbc;
  overflow: hidden;

  :first-child {
    border-bottom-left-radius: 10px;
    border-top-left-radius: 10px;
  }

  :last-child {
    border-bottom-right-radius: 10px;
    border-top-right-radius: 10px;
  }

  .state-run-block {
    height: 100%;
    background: #94ec8a;
    display: inline-block;
    vertical-align: top;
  }
  .state-idle-block {
    height: 100%;
    background: #FFD700;
    display: inline-block;
    vertical-align: top;
  }
  .state-pm-block {
    height: 100%;
    background: #4889fc;
    display: inline-block;
    vertical-align: top;
  }
  .state-stop-block {
    height: 100%;
    background: #9e3da5;
    display: inline-block;
    vertical-align: top;
  }
  .state-trouble-block {
    height: 100%;
    background: #ff5e5e;
    display: inline-block;
    vertical-align: top;
  }
}

.label-content {
  width: 250px;
  display: flex;
  justify-content: space-between;
}
</style>