// pages/applySettlement/applySettlement.js
import {
  allowedTypes
} from '../../utils/util'
import {
  merchantUpload,
  UPLOAD_IMG_BASE_URI,
  nextAreaInfo,
  merchantInfo,
  merchantAdd,
  merchantUpdate,
  bankList,
  banklinkList
} from '../../utils/request'
import moment from 'moment'
Page({

  /**
   * 页面的初始数据
   */
  data: {
    banklinkList: [],
    operationType: '1',
    fileList: [],
    licenceUrlList: [],
    bannerList: [],
    typeList: '',
    circleList: [],
    // 0表示类别，1表示商圈
    pickertype: 'typeList',
    showPop: false,
    mainTypeList: [{
        value: 10030,
        text: '企业商户',
      },
      {
        value: 10031,
        text: '个体工商户',
      },
      {
        value: 10033,
        text: '事业单位',
      },
      {
        value: 10034,
        text: '民办非企业',
      }
    ],
    accTypeList: [{
        value: 10070,
        text: '对公账户',
      },
      {
        value: 10071,
        text: '法人账户',
      },
    ],
    columns: [],
    showPop1: false,
    showStartTime: '',
    showEndTime: '',
    startTime: '00:00',
    endTime: '23:59',
    minHour: 0,
    maxHour: 23,
    currentDate: new Date().getTime(),
    currentendDate: new Date().getTime(),
    card: [],
    business: [],
    forma: function (type, value) {
      // if (type === 'hour') {
      //   return `${value}时`;
      // }
      // if (type === 'minute') {
      //   return `${value}分`;
      // }
      return value;
    },
    dragIndex: -1,
    startY: 0,
    currentIndex: -1,
    legalLicenceFrontList: [],
    legalLicenceBackList: [],
    openAccountLicenceList: [],
    businessBeginDate: '',
    businessEndDate: '',
    cardBeginDate: '',
    cardEndDate: '',
    selectcolumns: [],
    minDate: new Date(1900, 0, 1).getTime(),
    maxDate: new Date(2100, 0, 1).getTime()
  },
  inputChange(e) {
    console.log(e)
    const {
      type
    } = e.currentTarget.dataset
    const value = e.detail.value
    this.setData({
      [type]: value
    })
  },
  onChange: function (e) {
    this.setData({
      [e.currentTarget.dataset.type]: e.detail
    })
  },
  getinputvalue: function (e) {
    this.setData({
      searchValue: e.detail.value
    })
  },
  radioonChange: function (e) {
    this.setData({
      [e.currentTarget.dataset.type]: e.detail
    })
    if (e.detail.length != 0) {
      if (e.currentTarget.dataset.type == 'business') {
        this.setData({
          businessEndDate: ''
        })
      } else {
        this.setData({
          cardEndDate: ''
        })
      }
    }
  },
  onaddressChange(e) {
    console.log(e.detail, selectedOptions)
    const {
      value,
      selectedOptions
    } = e.detail
    this.getNextAreaInfo(value, selectedOptions)
  },
  showArea(e) {
    this.setData({
      showArea: true,
      addresstype: e.currentTarget.dataset.type
    })
    this.getNextAreaInfo(100000)
  },
  getNextAreaInfo(id, selectedOptions = []) {
    nextAreaInfo({
      searchId: id,
    }, resd => {
      if (resd && resd.code == 10000) {
        const data = resd.data.map(i => ({
          ...i,
          text: i.name,
          value: i.id,
        }));
        if (!data.length) return
        console.log(selectedOptions)
        // 如果是第一级（省份），直接设置 columns
        if (selectedOptions.length === 0) {
          this.setData({
            columns: this.data.columns.length ? this.data.columns : data
          });
          console.log(this.data.columns)
          return;
        }
        const columns = this.data.columns;
        const lastOption = selectedOptions[selectedOptions.length - 1];
        // 更新对应节点的 children
        console.log(columns)
        this.updateChildren(columns, lastOption.value, data);
        this.setData({
          columns
        });
        console.log(this.data.columns)
      }
    });
  },
  onClose(e) {
    console.log(e)
    const {
      type,
      fil
    } = e.currentTarget.dataset
    console.log(this.data)
    if (fil == 'cancel') {
      this.setData({
        [type]: false
      })
    } else {
      this.setData({
        [this.data.starttype]: moment(this.data.currentDate).format('YYYY-MM-DD'),
        [this.data.endtype]: moment(this.data.currentendDate).format('YYYY-MM-DD'),
        [type]: false
      })
    }
  },
  updateChildren(nodes, targetId, children) {
    nodes.some(node =>
      node.value === targetId ?
      (node.children = children, true) :
      node.children?.length && this.updateChildren(node.children, targetId, children)
    )
  },
  addshopptype: function () {
    const data = JSON.parse(JSON.stringify(this.data.tableList))
    data.push({})
    this.setData({
      tableList: data
    })
  },
  showtypemodel: function () {
    this.setData({
      classtypemodal: true
    })
  },
  closetypemodel: function () {
    this.setData({
      classtypemodal: false
    })
  },
  closeType: function () {

  },
  jump: function (e) {
    const {
      url
    } = e.currentTarget.dataset
    wx.navigateTo({
      url: url,
    })
  },
  onInput: function (e) {
    this.setData({
      [e.currentTarget.dataset.field]: e.detail
    })
  },
  comfrimDate: function (e) {
    if (this.data.currentendDate < this.data.currentDate) {
      wx.showToast({
        title: '结束时间不能小于开始时间',
        icon: 'none'
      })
      return
    }
    if (this.data.xType == 'card') {
      this.setData({
        card: []
      })
    } else {
      this.setData({
        business: []
      })
    }
    // this.setData({
    //   [this.data.starttype]: this.data[starttype],
    //   [this.data.endtype]: this.data[endtype]
    // })
    this.onClose(e)
  },
  clearItem(e) {
    const {
      value,
      pickertype
    } = e.currentTarget.dataset
    this.setData({
      [pickertype]: this.data[pickertype]?.filter(i => i.value != value)
    })
  },
  onConfirm(e) {
    const {
      pickertype
    } = this.data
    const {
      value,
      text
    } = e.detail.value
    console.log(pickertype, e.detail.value)
    if (pickertype == 'bank') {
      this.setData({
        bankName: text
      })
    }
    if (pickertype == 'bankLinkNo') {
      this.setData({
        bankBranch: text
      })
    }
    this.setData({
      [pickertype]: value,
      [pickertype + 'Text']: text,
      showPop: false,
    })
  },

  openTime(e) {
    console.log(e)
    this.setData({
      starttype: e.currentTarget.dataset.start,
      endtype: e.currentTarget.dataset.end,
      showPop1: true,
      xType: e.currentTarget.dataset.type
    })
  },
  openPop(e) {
    const {
      pickertype
    } = e.currentTarget.dataset
    const {
      mainTypeList,
      accTypeList
    } = this.data
    let columns = []
    // 行业类别
    if (pickertype == 'mainType') {
      columns = mainTypeList
    } else if (pickertype == 'accType') {
      columns = accTypeList
    } else if (pickertype == 'bank') {
      columns = this.data.bankList
    }
    if (pickertype == 'bankLinkNo' && !this.data.province) {
      wx.showToast({
        title: '请选择开户行',
        icon: 'none'
      })
      return
    }
    this.setData({
      showPop: true,
      selectcolumns: columns,
      pickertype,
      searchValue: ''
    })
    if (pickertype == 'bankLinkNo' && this.data.province) {
      this.getbanklinkList('e')
    }
  },
  // 上传校验
  beforeRead(event) {
    const {
      file,
      callback
    } = event.detail;
    console.log(event.detail)
    const fileExtension = file.url ? file.url.substring(file.url.lastIndexOf('.')).toLowerCase() : '';

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
    const {
      type
    } = event.currentTarget.dataset
    console.log(file)
    // 当设置 mutiple 为 true 时, file 为数组格式，否则为对象格式
    const list = this.data[type]
    const fs = wx.getFileSystemManager();
    // 读取文件并转base64
    fs.readFile({
      filePath: file.tempFilePath,
      encoding: 'base64', // 关键配置：指定编码为base64
      success: (res) => {
        // 拼接MIME前缀（根据图片类型调整，默认png/jpg均可）
        const base64 = `data:image/jpeg;base64,${res.data}`;
        merchantUpload({
          fileStr: base64
        }, resd => {
          if (resd && resd.code == 10000) {
            wx.showToast({
              title: '上传成功!',
              icon: 'success'
            })
            list.push({
              ...file,
              url: resd.data
            });
            this.setData({
              [type]: list,
            });
            console.log(this.data)
          }
        })
        // 此处可直接上传base64到服务器

      },
      fail: (err) => {
        wx.showToast({
          title: '转换失败',
          icon: 'none'
        });
        console.error('转换错误：', err);
      }
    });
  },
  deleteRead: function (e) {
    const {
      type
    } = e.currentTarget.dataset
    const index = e.detail.index
    this.setData({
      [type]: this.data[type].filter((item, i) => i != index)
    })
  },
  goBack: function () {
    wx.navigateBack({
      delta: 1,
    });
  },
  getbanklinkList: function () {
    banklinkList({
      bank: this.data.bank,
      province: this.data.province,
      city: this.data.city
    }, resd => {
      resd.data.map(resd => {
        resd.value = resd.bankLinkNo
        resd.text = resd.bankLinkName
      })
      this.setData({
        banklinkList: resd.data,
        selectcolumns: resd.data
      })
    })
  },
  onFinish(e) {
    const {
      selectedOptions,
      value
    } = e.detail;
    console.log(selectedOptions)
    if (selectedOptions.length < 3 && this.data.addresstype != 'bankAddress') return
    if (selectedOptions.length < 2 && this.data.addresstype == 'bankAddress') return
    this.setData({
      targetAddress: selectedOptions.reduce((pre, cur) => {
        pre[cur.level == 1 ? 'province' : cur.level == 2 ? 'city' : 'district'] = cur.name
        return pre
      }, {}),
      [this.data.addresstype]: selectedOptions.map(i => i.name).join(''),
      showArea: false,
    })
    if (this.data.addresstype == 'storeAddrInfo') {
      this.setData({
        storeProvince: selectedOptions[0].id,
        storeCity: selectedOptions[1].id,
        storeCounty: selectedOptions[2].id,
      })
      console.log(this.data)
    }
    if (this.data.addresstype == 'businessAddrInfo') {
      this.setData({
        businessProvince: selectedOptions[0].id,
        businessCity: selectedOptions[1].id,
        businessCounty: selectedOptions[2].id,
      })
    }
    if (this.data.addresstype == 'bankAddress') {
      this.setData({
        province: selectedOptions[0].id,
        city: selectedOptions[1].id,
      })
    }
  },
  submit: function (e) {
    const params = {
      shopId: this.data.shopId,
      status: +e.currentTarget.dataset.status,
      operationType: this.data.operationType,
      merchantName: this.data.merchantName,
      contactPhone: this.data.contactPhone,
      email: this.data.email,
      cardName: this.data.cardName,
      cardNo: this.data.cardNo,
      cardMobile: this.data.cardMobile,
      cardBeginDate: this.data.cardBeginDate,
      cardEndDate: this.data.card.length ? '长期' : this.data.cardEndDate,
      credentialsType: this.data.credentialsType,
      storeName: this.data.storeName,
      storeProvince: this.data.storeProvince,
      storeCity: this.data.storeCity,
      storeCounty: this.data.storeCounty,
      storeAddr: this.data.storeAddr,
      businessName: this.data.businessName,
      businessNo: this.data.businessNo,
      mainType: this.data.mainType,
      legalPerson: this.data.legalPerson,
      businessProvince: this.data.businessProvince,
      businessCity: this.data.businessCity,
      businessCounty: this.data.businessCounty,
      businessAddr: this.data.businessAddr,
      businessBeginDate: this.data.businessBeginDate,
      businessEndDate: this.data.business.length ? '长期' : this.data.businessEndDate,
      accType: this.data.accType,
      accCardNo: this.data.accCardNo,
      accMobile: this.data.accMobile,
      accName: this.data.accName,
      bank: this.data.bank,
      bankName: this.data.bankName,
      bankLinkNo: this.data.bankLinkNo,
      bankBranch: this.data.bankBranch,
      province: this.data.province,
      city: this.data.city,
      legalLicenceFrontUrl: this.data.legalLicenceFrontList.length ? this.data.legalLicenceFrontList.map(aa => aa.url).join() : '',
      legalLicenceBackUrl: this.data.legalLicenceBackList.length ? this.data.legalLicenceBackList.map(aa => aa.url).join() : '',
      openAccountLicenceUrl: this.data.openAccountLicenceList.length ? this.data.openAccountLicenceList.map(aa => aa.url).join() : '',
      licenceUrl: this.data.licenceUrlList.length ? this.data.licenceUrlList.map(aa => aa.url).join() : '',
    }
    if (this.data.merchantId) {
      params.id = this.data.merchantId
    }
    if (this.data.merchantId) {
      merchantUpdate({
        ...params
      }, resd => {
        if (resd.code == 10000) {
          wx.showToast({
            title: resd.msg,
            icon: 'success'
          })
          setTimeout(_ => {
            wx.navigateBack()
          }, 500)
        }
      })
    } else {
      merchantAdd({
        ...params
      }, resd => {
        if (resd.code == 10000) {
          wx.showToast({
            title: resd.msg,
            icon: 'success'
          })
          setTimeout(_ => {
            wx.navigateBack()
          }, 500)
        }
      })
    }
  },
  getmerchantInfo: function () {
    merchantInfo({
      searchId: this.data.merchantId,
      searchField1: this.data.shopId
    }, resd => {
      if (resd.code == 10000) {
        console.log(this.data.columns)
        if (resd?.data.status == 1 && this.data.type != 'find') {
          wx.redirectTo({
            url: `/pages/merapplyresult/merapplyresult?merchantId=${this.data.merchantId}&shopId=${this.data.shopId}`,
          })
        } else {
          const data = resd?.data || {}
          if (data.province) { //开户所属地区赋值
            nextAreaInfo({
              searchId: data.province,
            }, resd => {
              if (resd && resd.code == 10000) {
                this.setData({
                  bankAddress: data.province ? `${this.data.columns.filter(ax=>ax.id==data.province)[0]?.name}${resd.data.filter(ax=>ax.id==data.city)[0]?.name}` : '',
                })
              }
            });
          }
          if (data.storeProvince) { //开户所属地区赋值
            nextAreaInfo({
              searchId: data.storeProvince,
            }, resd => {
              if (resd && resd.code == 10000) {
                this.setData({
                  storeAddrInfo: data.storeProvince ? `${this.data.columns.filter(ax=>ax.id==data.storeProvince)[0]?.name}${resd.data.filter(ax=>ax.id==data.storeCity)[0]?.name}` : '',
                })
                nextAreaInfo({
                  searchId: data.storeCity,
                }, resd => {
                  if (resd && resd.code == 10000) {
                    this.setData({
                      storeAddrInfo: this.data.storeAddrInfo += `${resd.data.filter(ax=>ax.id==data.storeCounty)[0]?.name}`,
                    })

                  }
                });
              }
            });
          }
          if (data.businessProvince) { //开户所属地区赋值
            nextAreaInfo({
              searchId: data.businessProvince,
            }, resd => {
              if (resd && resd.code == 10000) {
                this.setData({
                  businessAddrInfo: data.businessProvince ? `${this.data.columns.filter(ax=>ax.id==data.businessProvince)[0]?.name}${resd.data.filter(ax=>ax.id==data.businessCity)[0]?.name}` : '',
                })
                nextAreaInfo({
                  searchId: data.businessCity,
                }, resd => {
                  if (resd && resd.code == 10000) {
                    this.setData({
                      businessAddrInfo: this.data.businessAddrInfo += `${resd.data.filter(ax=>ax.id==data.businessCounty)[0]?.name}`,
                    })

                  }
                });
              }
            });
          }
          this.setData({
            card: data.cardEndDate == '长期' ? ['1'] : [],
            business: data.businessEndDate == '长期' ? ['1'] : [],
            operationType: data.operationType,
            merchantName: data.merchantName,
            contactPhone: data.contactPhone,
            email: data.email,
            cardMobile: data.cardMobile,
            cardName: data.cardName,
            cardNo: data.cardNo,
            cardBeginDate: data.cardBeginDate,
            cardEndDate: data.cardEndDate != '长期' ? data.cardEndDate : '',
            credentialsType: data.credentialsType ? data.credentialsType : '',
            storeName: data.storeName,
            storeProvince: data.storeProvince,
            storeCity: data.storeCity,
            storeCounty: data.storeCounty,
            storeAddr: data.storeAddr,
            businessName: data.businessName,
            businessNo: data.businessNo,
            mainType: data.mainType ? data.mainType : '',
            mainTypeText: this.data.mainTypeList.filter(ax => ax.value == data.mainType)[0]?.text,
            legalPerson: data.legalPerson,
            businessProvince: data.businessProvince,
            businessCity: data.businessCity,
            businessCounty: data.businessCounty,
            businessAddr: data.businessAddr,
            businessBeginDate: data.businessBeginDate,
            businessEndDate: data.businessEndDate != '长期' ? data.businessEndDate : '',
            accType: data.accType ? data.accType : '',
            accCardNo: data.accCardNo,
            accMobile: data.accMobile,
            accName: data.accName,
            bank: data.bank,
            bankName: data.bankName,
            bankLinkNo: data.bankLinkNo,
            bankBranch: data.bankBranch,
            province: data.province,
            city: data.city,
            legalLicenceFrontList: data.legalLicenceFrontUrl ? data.legalLicenceFrontUrl.split(',').map(xzc => {
              return {
                isImage: true,
                url: `${xzc}`
              }
            }) : [],
            legalLicenceBackList: data.legalLicenceBackUrl ? data.legalLicenceBackUrl.split(',').map(xzc => {
              return {
                isImage: true,
                url: `${xzc}`
              }
            }) : [],
            openAccountLicenceList: data.openAccountLicenceUrl ? data.openAccountLicenceUrl.split(',').map(xzc => {
              return {
                isImage: true,
                url: `${xzc}`
              }
            }) : [],
            licenceUrlList: data.licenceUrl ? data.licenceUrl.split(',').map(xzc => {
              return {
                isImage: true,
                url: `${xzc}`
              }
            }) : [],
          })
        }
      } else {

      }
    })
  },
  getbankList: function () {
    bankList({}, resd => {
      const data = resd.data.filter(ax => ax.bank)
      data.map(asd => {
        asd.value = asd.bank
        asd.text = asd.bankName
      })
      this.setData({
        backAllList: data,
        bankList: data
      })
    })
  },
  searchyinhang: function (e) {
    this.setData({
      searchValue: e.detail.value
    })
    if (e.detail.value) {
      this.setData({
        selectcolumns: this.data.pickertype == 'bank' ? this.data.backAllList.filter(xx => xx.text.indexOf(e.detail.value) != -1) : this.data.banklinkList.filter(xx => xx.text.indexOf(e.detail.value) != -1)
      })
    } else {
      this.setData({
        selectcolumns: this.data.pickertype == 'bank' ? this.data.backAllList : this.data.banklinkList
      })
    }
  },
  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {
    this.getNextAreaInfo(100000)
    this.getbankList()
    const app = getApp()
    this.setData({
      type: options.type,
      merchantId: +options.merchantId,
      isIOS: app.globalData.isIOS,
      shopId: options?.shopId ? +options?.shopId : undefined
    })
    if (this.data.merchantId) {
      this.getmerchantInfo()
    }
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