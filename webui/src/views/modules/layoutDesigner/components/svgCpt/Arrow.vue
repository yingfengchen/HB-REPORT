<template>
  <g :x="height" :y="height">
    <path
        :d="'m'+(- (height*x_offset))+','+(height*y_offset)+'l'+height+',-'+height*1.5+'l'+height+','+height*1.5+'l-'+height/2+',0l0,'+height*1.5+'l-'+height+',0l0,-'+height*1.5+'l-'+height/2+',0l0,0l0,0z'"
        transform="rotate(90 40 60)" :fill="color" :stroke="strokeColor" :stroke-width="width"/>
  </g>
</template>

<script>
export default {
  name: "Arrow",
  props: {
    'height': {
      type: Number,
      default: 60
    },
    'color': {
      type: String,
      default: '#DDDDDD'
    },
    'width': {
      type: Number,
      default: 40
    },
    'strokeColor': {
      type: String,
      default: '#000000'
    }
  },
  data() {
    return {
      x_offset: 1.5,
      y_offset: 2.5
    }
  }
}
</script>

<style scoped>

</style>
