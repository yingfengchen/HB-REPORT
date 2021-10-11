<template>
  <g>
    <rect :stroke-width="width" :height="height/4" :width="height" :y="-(height/8)" :x="-(height/2)" stroke="#000000" :fill="color" rx="4" />
    <rect :height="height/4*0.8" :width="height*0.9" :y="-(height/4*0.8/2)" :x="-(height*0.9/2)" :stroke-width="width" stroke="#000000" fill="#ffffff" />
    <text transform="matrix(1 0 0 1 0 0)" xml:space="preserve" font-weight="normal" text-anchor="middle" font-family="sans-serif" :font-size="144*(height/1450)" :y="height/4*0.8/5" :x="0" stroke="#000000" fill="#000000">&nbsp;</text>
  </g>
</template>

<script>
export default {
  name: 'EQSlot',
  // eslint-disable-next-line vue/require-prop-types
  props: ['height', 'color', 'width']
}
</script>

<style scoped>

</style>
