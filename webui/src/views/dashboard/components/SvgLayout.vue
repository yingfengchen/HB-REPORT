<template>
  <div id="svg_layout" class="layout" />
</template>

<script>
import * as d3 from 'd3'
import { getLayout } from '@api/api'

export default {
  name: 'SvgLayout',
  mounted() {
    getLayout({layout_name: 'HB_01'}).then(res => {
      if(res['success']) {
        this.initLayout(res['result'])
      }else{
        this.$notification['error']({
          message: '获取数据失败',
          description: res['message'],
        });
      }
    })
  },
  methods: {
    initLayout(svg_str = '<svg xmlns="http://www.w3.org/2000/svg" xmlns:svg="http://www.w3.org/2000/svg"></svg>') {
      const xml_obj = this.xmlStr2XmlObj(svg_str)
      if (xml_obj.children.length > 0) {
        xml_obj.children[0].id = 'machine_layout'
        document.getElementById('svg_layout').innerHTML = ''
        document.getElementById('svg_layout').appendChild(xml_obj.children[0])
      }
      const ele_ml = document.getElementById('machine_layout')
      if (ele_ml) {
        ele_ml.setAttribute('width', '100%')
        ele_ml.setAttribute('height', '100%')
      }
    },
    zoomimg() {
      const svg = d3.select('#machine_layout')
        .call(d3.zoom().on('zoom', (event) => {
          svg.selectAll('g').attr('transform', event.transform)
        }))
    },
    xmlStr2XmlObj(xmlStr) {
      let xmlObj = {}
      if (document.all) {
        // eslint-disable-next-line no-undef
        const xmlDom = new ActiveXObject('Microsoft.XMLDOM')
        xmlDom.loadXML(xmlStr)
        xmlObj = xmlDom
      } else {
        xmlObj = new DOMParser().parseFromString(xmlStr, 'text/xml')
      }
      return xmlObj
    }
  }
}
</script>

<style scoped>
.layout {
  border-radius: 4px;
  overflow: hidden;
}
</style>
