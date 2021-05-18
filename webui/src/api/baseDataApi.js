import { getAction, deleteAction, putAction, postAction, httpAction } from '@/api/manage'

//获取tag信息列表
const getTagList = (params) => postAction('/base/getTagList', params)
//插入tag
const insertTags = (params) => postAction('/base/insertTagDefBunch', params)
//插入tag
const deleteTags = (params) => postAction('/base/deleteTags', params)

export {
  getTagList,
  insertTags,
  deleteTags
}