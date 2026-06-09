// pages/qualificationVerify/qualificationVerify.js
import { selectVerificationRecordList,addVerificationRecord,updateVerificationRecord,uploadImages,UPLOAD_IMG_BASE_URI } from '../../utils/request' 
import { validateField,allowedTypes } from '../../utils/util'
Page({

  /**
   * 页面的初始数据
   */
  data: {
    fileList: [],
    otherList: [],
    verificationData: [],
    contactPhone: '',
    contactEmail: '',
  },
  inputChange:function(e){
    const { type } = e.currentTarget.dataset
    this.setData({
      [type]: e.detail
    })
  },
  goBack() {
    wx.navigateBack({
      delta: 1, 
    });
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
      file
    } = event.detail;
    const type = event.currentTarget.dataset.type
    const images = this.data[type]
    // 当设置 mutiple 为 true 时, file 为数组格式，否则为对象格式
    uploadImages(file.url,resd=>{
      if (resd && resd.code == 10000) {
        wx.showToast({
          title: '上传成功!',
          icon: 'success'
        })
        images.push({
          url: UPLOAD_IMG_BASE_URI + resd.data.url
        });
        this.setData({
          [type]: images,
        });
      }
    })
  },
  deleteRead: function (e) {
    const index = e.detail.index
    const type = e.currentTarget.dataset.type
    this.setData({
      [type]: this.data[type].filter((item, i) => i != index)
    })
  },
  /**
   * 生命周期函数--监听页面加载
   */
  getSelectVerificationRecordList:function(){
    console.log(23123)
    selectVerificationRecordList({
      searchId: +this.data.teamId,
    },resd=>{
      if(resd && resd.code == 10000){
        const data = resd.data || {}
        console.log(data)
        this.setData({
          verificationData: data,
          contactPhone: data?.contactPhone || this.data.defaultContactPhone,
          contactEmail: data?.contactEmail || this.data.defaultContactEmail,
          fileList: data?.licenseImage ? data?.licenseImage?.split(';').map(i=>({
            url: i,
          })):[],
          otherList: data?.additionPicture ? data?.additionPicture?.split(';').map(i=>({
            url: i,
          })):[]
        })
      }
    })
  },
  comfirm:function(){
    const { verificationData } = this.data
    const params = {
      licenseImageList: this.data.fileList?.map(i=>i.url) || [],
      additionPictureList: this.data.otherList?.map(i=>i.url) || [],
      contactPhone: this.data.contactPhone,
      contactEmail: this.data.contactEmail || undefined,
      licenseType: 0,
      type: 0,
      verificationType: this.data.teamType,
      teamId: this.data.teamId,
      isUserAuth: this.data.isUserAuth == '是' ? 1 : 0,
    }
    const title = validateField(params)
    if(title){
      wx.showToast({
        title,
        icon: 'none'
      })
      return
    }
    // 判断是添加还是更新，存在资质id则是更新，否则是添加
    if(verificationData?.id){
      updateVerificationRecord({
        ...params,
        id: verificationData?.id,
      },resd=>{
        if(resd && resd.code == 10000){
          wx.showToast({
            title: '更新成功！',
            icon: 'success',
          })
          this.getSelectVerificationRecordList()
        }
      })
    }else{
      addVerificationRecord({
        ...params
      },resd=>{
        if(resd && resd.code == 10000){
          wx.showToast({
            title: '添加成功!',
            icon: 'success',
          })
          this.getSelectVerificationRecordList()
        }
      })
    }
  },
  onLoad(options) {
    const { teamId, teamType, contactPhone, contactEmail, isUserAuth } = options
    this.setData({
      teamId: +teamId,
      teamType: +teamType,
      defaultContactPhone: contactPhone || '',
      defaultContactEmail: contactEmail || '',
      isUserAuth: isUserAuth || '是',
    })
    this.getSelectVerificationRecordList()
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