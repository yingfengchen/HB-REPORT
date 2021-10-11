<template>
  <g>
    <rect stroke="#000000" :fill="color" :x="-(height/20 - height/20 - height/10)" :y="-(height/2)" :width="height/10" :height="height" :stroke-width="width" />
    <rect stroke="#000000" :fill="color" :x="-(height/20 - height/20)" :y="-(height/2)" :width="height/10" :height="height" :stroke-width="width" />
    <rect stroke="#000000" :fill="color" :x="-(height/20 + height/20)" :y="-(height/2)" :width="height/10" :height="height" :stroke-width="width" />
    <rect stroke="#000000" :fill="color" :x="-(height/20 + height/20 + height/10)" :y="-(height/2)" :width="height/10" :height="height" :stroke-width="width" />
  </g>
</template>

<script>
export default {
  name: 'Conveyor',
  // eslint-disable-next-line vue/require-prop-types
  props: ['height', 'color', 'width']
}
</script>

<style scoped>

</style>
