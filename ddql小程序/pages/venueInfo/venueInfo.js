// pages/venueInfo/venueInfo.js
import {
  selectUserPlaces,
  updateUserPlaces,
  uploadImages,
  UPLOAD_IMG_BASE_URI,
  placeEquipment
} from '../../utils/request'
import {
  allowedTypes
} from '../../utils/util'
Page({

  /**
   * 页面的初始数据
   */
  data: {
    active: 0,
    venueList: [],
    placeName: '',
    images: [],
    remark: '',
  },
  // 上传校验
  beforeRead(event) {
    const {
      file,
      callback
    } = event.detail;
    console.log(file.fileType)
    const fileExtension = file.url ? file.url.substring(file.url.lastIndexOf('.')).toLowerCase() : '';
    console.log(fileExtension)
    const isValidExtension = file.url ? allowedTypes.includes(fileExtension) : false;
    // 回调结果
    callback(file.fileType == 'image' && isValidExtension);
    // 如果不合法，可以给出提示
    if (!isValidExtension) {
      wx.showToast({
        title: '只支持jpg、jpeg、png格式',
        icon: 'none',
        duration: 2000
      });
    }
  },
  afterRead(event) {
    const {
      images = []
    } = this.data;
    const {
      file
    } = event.detail;
    console.log(file)
    // 当设置 mutiple 为 true 时, file 为数组格式，否则为对象格式
    uploadImages(file.url,resd=>{
      if (resd && resd.code == 10000) {
        wx.showToast({
          title: '上传成功!',
          icon: 'success'
        })
        images.push({
          ...file,
          url: UPLOAD_IMG_BASE_URI + resd.data.url
        });
        this.setData({
          images
        });
      }
    })
  },
  deleteRead: function (e) {
    const index = e.detail.index
    this.setData({
      images: this.data.images.filter((item, i) => i != index)
    })
  },
  save: function () {
    const { active,placeName,images,remark } = this.data
    if(!placeName || !remark){
      wx.showToast({
        title: '场地名称和场地介绍必填！',
        icon: 'none',
      })
      return
    }
    updateUserPlaces({
      id: active,
      name: placeName,
      images: images.map(i=>i.url),
      introduction: remark,
    },resd=>{
      if(resd && resd.code == 10000){
        wx.showToast({
          title: '保存成功!',
          icon: 'success'
        })
        this.getselectUserPlaces()
      }
    })
  },
  getselectUserPlaces: function () {
    selectUserPlaces({}, resd => {
      if (resd && resd.code == 10000) {
        const data = resd.data
        const item = this.data.active? data.find(i=>i.id == this.data.active) : data[0]
        this.setData({
          venueList: data,
          active: item.id,
          placeName: item.name,
          images: item.images.split(';').map(i => ({
            url: i
          })),
          remark: item.introduction.trim().replace(/^(.+)$/gm, "  $1")
        })
      }
    })
  },
  change:function(e){
    let {
      value
    } = e.detail
    const type = e.currentTarget.dataset.type
    this.setData({
      [type]: type=='placeName' ? value.trim() : value.trim().replace(/^(.+)$/gm, "  $1"),
    })
  },
  getPlaceEquipment(){
    placeEquipment({
      searchId: this.data.active
    },resd=>{
      if(resd && resd.code == 10000){
        this.setData({
          equipmentId: resd.data?.serialNumber || ''
        })
      }
    })
  },
  onChange: function (e) {
    const {
      venueList
    } = this.data
    const id = e.detail.name
    const item = venueList.find(i => i.id == id)
    this.setData({
      active: id,
      placeName: item.name,
      images: item.images.split(';').map(i => ({
        url: i
      })),
      remark: item.introduction?.trim().replace(/^(.+)$/gm, "  $1") || ''
    })
    this.getPlaceEquipment()
  },
  goBack: function () {
    wx.navigateBack({
      delta: 1,
    });
  },
  jump: function (e) {
    const {
      url,
    } = e.currentTarget.dataset
    wx.navigateTo({
      url:`${url}&type=place`,
    })
  },
  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {
    // 获取关联的场所
    this.getselectUserPlaces()
    this.getPlaceEquipment()
  },

  /**
   * 生命周期函数--监听页面初次渲染完成
   */
  onReady() {

  },

  /**
   * 生命周期函数--监听页面显示
   */
  onShow() {

  },

  /**
   * 生命周期函数--监听页面隐藏
   */
  onHide() {

  },

  /**
   * 生命周期函数--监听页面卸载
   */
  onUnload() {

  },

  /**
   * 页面相关事件处理函数--监听用户下拉动作
   */
  onPullDownRefresh() {

  },

  /**
   * 页面上拉触底事件的处理函数
   */
  onReachBottom() {

  },

  /**
   * 用户点击右上角分享
   */
  onShareAppMessage() {

  }
})