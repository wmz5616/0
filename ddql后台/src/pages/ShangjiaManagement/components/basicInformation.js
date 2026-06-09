import React from 'react';
import { PlusOutlined, MinusCircleOutlined } from '@ant-design/icons';
import {
  Form,
  Input,
  Button,
  Row,
  Col,
  Spin,
  message,
  Popconfirm,
  Select,
  DatePicker,
  Upload,
  Modal,
  Radio,
  Switch,
  TimePicker,
  Alert,
  InputNumber,
  Table,
} from 'antd';
import { DndProvider, DragSource, DropTarget } from 'react-dnd';
import { history, connect, Link } from 'umi';
import ImgCrop from 'antd-img-crop';
import Map from '@/components/Map';
import CKEditor from 'react-ckeditor-wrapper';
import { HTML5Backend } from 'react-dnd-html5-backend';
import update from 'immutability-helper';
import dayjs from 'dayjs';
import { urlName } from '@/utils/utils';
const { TextArea } = Input;
// import { setToken } from '@/utils/authority';

const { Option } = Select;
const { RangePicker } = DatePicker;
// 场所基础信息

let dragingIndex = -1;
class BodyRow extends React.Component {
  state = {
    updataCloneList: [],
  };

  render() {
    const { isOver, connectDragSource, connectDropTarget, moveRow, ...restProps } = this.props;
    const style = { ...restProps.style, cursor: 'move' };

    let { className } = restProps;
    if (isOver) {
      if (restProps.index > dragingIndex) {
        className += ' drop-over-downward';
      }
      if (restProps.index < dragingIndex) {
        className += ' drop-over-upward';
      }
    }

    return connectDragSource(
      connectDropTarget(<tr {...restProps} className={className} style={style} />),
    );
  }
}

const rowSource = {
  beginDrag(props) {
    dragingIndex = props.index;
    return {
      index: props.index,
    };
  },
};

const rowTarget = {
  drop(props, monitor) {
    const dragIndex = monitor.getItem().index;
    const hoverIndex = props.index;

    // Don't replace items with themselves
    if (dragIndex === hoverIndex) {
      return;
    }

    // Time to actually perform the action
    props.moveRow(dragIndex, hoverIndex);

    // Note: we're mutating the monitor item here!
    // Generally it's better to avoid mutations,
    // but it's good here for the sake of performance
    // to avoid expensive index searches.
    monitor.getItem().index = hoverIndex;
  },
};

const DragableBodyRow = DropTarget('row', rowTarget, (connect, monitor) => ({
  connectDropTarget: connect.dropTarget(),
  isOver: monitor.isOver(),
}))(
  DragSource('row', rowSource, (connect) => ({
    connectDragSource: connect.dragSource(),
  }))(BodyRow),
);

class AddShopMsgModal extends React.Component {
  formRef = React.createRef();
  formRefs = React.createRef();
  state = {
    spinning: false,
    pageNum: 1,
    list: [],
    thumbnailFileList: [],
    carouselFileList: [],
    locations: {},
    longitude: 113.880469,
    latitude: 22.889404,
    addGroupModalVisible: false,
    majorList: [],
    managerList: [],
    reviewVisible: false,
    searchType: 1,
    fastIndex: -1,
    fastSelectList: [
      {
        title: '一个月',
        value: 1,
      },
      {
        title: '三个月',
        value: 3,
      },
      {
        title: '六个月',
        value: 6,
      },
      {
        title: '一年',
        value: 12,
      },
      {
        title: '自定义',
        value: 0,
      },
    ],
  };

  components = {
    body: {
      row: DragableBodyRow,
    },
  };

  componentDidMount() {
    const { type, id, disabled = false, auditId = undefined } = this.props;
    this.setState(
      {
        type,
        id,
        disabled,
        auditId,
      },
      () => {
        type != 'add' && this.getData();
        id && type != 'review' && type != 'info' && this.getManagerList();
      },
    );
    this.getBusinessType();
  }

  getManagerList = () => {
    this.props.dispatch({
      type: 'myModel/getSetData',
      payload: {
        searchField1: this.state.id,
      },
      url: `/ddql/business/shop/manager/get`,
      method: 'POST',
      myData: (res) => {
        if (res && res.code === 10000) {
          this.setState({
            managerList: res.data,
          });
        }
      },
    })
  }

  getData = () => {
    this.setState({
      spinning: true,
    });
    this.props.dispatch({
      type: 'myModel/getSetData',
      payload: {
        searchId: this.state.type == 'edit' ? this.state.id : this.state.auditId,
      },
      url: this.state.type == 'edit'
        ? `/ddql/business/shop/selectById`
        : `/ddql/audit/get`,
      method: 'POST',
      myData: (res) => {
        this.setState({
          spinning: false,
        });
        if (res && res.code == 10000) {
          const values = res.data.shop;
          const carouselFileList = [];
          const carouselImageUrls = values.galleryImages;
          carouselImageUrls.map((ress, index) => {
            carouselFileList.push({
              uid: String(index + 1),
              name: `image${index}.png`,
              status: 'done',
              url: ress,
              response: { data: { url: ress } },
            });
          });
          const industryCategoryIds = res.data.industryList
          this.formRef.current.setFieldsValue({
            status: values.status == 1 ? true : false,
            remark: values.remark,
            name: values.name,
            userName: values.userName ? values.userName : '',
            topRecommend: values.topRecommend,
            topConsumption: values.topConsumption,
            recommendOrder: values.recommendOrder ? values.recommendOrder : 0,
            phone: values.phone ? values.phone : '',
            sortOrder: values.sortOrder,
            time: values.startTime
              ? [dayjs(values.startTime, 'HH:mm'), dayjs(values.endTime, 'HH:mm')]
              : undefined,
            circleIds: res.data.circleList.map((x) => x.circleId),
            industryCategoryIds: industryCategoryIds.map(
              (x) => x.industryCategoryId,
            ),
            topConsumptionStartTime: values.topConsumptionStartTime
              ? dayjs(values.topConsumptionStartTime)
              : undefined,
            topConsumptionEndTime: values.topConsumptionEndTime
              ? dayjs(values.topConsumptionEndTime)
              : undefined,
          });
          this.setState({
            topRecommend: values.topRecommend,
            topConsumption: values.topConsumption,
            location: values.location,
            id: values.id,
            thumbnailFileList: [
              {
                uid: '1',
                name: 'image.png',
                status: 'done',
                url: values.coverImageUrl,
                response: { data: { url: values.coverImageUrl } },
              },
            ],
            carouselFileList: carouselFileList,
            content: values.description,
            locationInfo: { address: values.address },
            locationStr: values.location,
            locationValue: values.address,
            topStartTime: values.topStartTime ? dayjs(values.topStartTime) : undefined,
            topEndTime: values.topEndTime ? dayjs(values.topEndTime) : undefined,
            fastIndex: this.monthGap(values.topStartTime, values.topEndTime),
            managerList: this.state.type == 'edit' ? this.state.managerList : res.data.shopManagerList,
          });
        } else {
          message.error(res.msg);
        }
      },
    });
  };

  monthGap = (startDate, endDate) => {
    if (!startDate || !endDate) {
      return -1;
    }
    const start = dayjs(startDate);
    const end = dayjs(endDate);

    const startYear = start.year();
    const startMonth = start.month();
    const endYear = end.year();
    const endMonth = end.month();

    const gapMonth = (endYear - startYear) * 12 + (endMonth - startMonth);
    // 找不到就是自定义
    const index = this.state.fastSelectList.findIndex((i) => i.value == gapMonth);

    return index == -1 ? 0 : index;
  };

  updateContent = (value, index) => {
    this.setState({
      content: value,
    });
  };
  showModal = () => {
    this.setState(
      {
        map: true,
      },
      () => {
        this.toolEvents = {
          created: (tool) => {
            this.tool = tool;
          },
        };
        // this.mapPlugins = ['ToolBar'];
        this.mapCenter = { longitude: 120, latitude: 35 };
        this.markerPosition = { longitude: 121, latitude: 36 };
      },
    );
  };
  handleCancelz = () => {
    this.setState({
      map: false,
    });
  };
  handleOkz = () => {
    if (!this.state.locationInfo) {
      message.info('请选择所在位置');
      return;
    }
    this.setState(
      {
        map: false,
      },
      () => {
        this.setState({
          locationValue: this.state.locationInfo.address + this.state.locationInfo.name,
        });
      },
    );
  };
  getAddress = () => {
    setTimeout((_) => {
      window.AMap.plugin('AMap.PlaceSearch', () => {
        var placeSearch = new window.AMap.PlaceSearch({
          pageSize: 10,
          pageIndex: 1,
          // citylimit: true, // 仅搜索本城市的地名
        });
        placeSearch.search(document.getElementById('tipinputs').value, (res, result) => {
          if (result.poiList.pois.length == 0) {
            message.info('未搜索到相关地址，请重新输入');
            return;
          }
          this.setState(
            {
              AddressDetails:
                result.poiList &&
                result.poiList.pois &&
                result.poiList.pois[0] &&
                result.poiList.pois[0].location,
              latitude:
                result.poiList &&
                result.poiList.pois &&
                result.poiList.pois[0] &&
                result.poiList.pois[0].location.lat,
              longitude:
                result.poiList &&
                result.poiList.pois &&
                result.poiList.pois[0] &&
                result.poiList.pois[0].location.lng,
              locations: result.poiList && result.poiList.pois && result.poiList.pois[0],
              location: `${result.poiList.pois[0].location.lng},${result.poiList.pois[0].location.lat}`,
            },

            () => { },
          );
        });
      });
    }, 150);
  };

  selectAddress = {
    // created必须要拥有,用来初始化创建相应对象
    created: () => {
      let auto;
      let placeSearch;
      window.AMap.plugin('AMap.Autocomplete', () => {
        auto = new window.AMap.Autocomplete({
          input: 'tipinputs',
          pageSize: 10,
          pageIndex: 1,
          // citylimit: true, // 仅搜索本城市的地名
          // city: '昆明', // 限制为只能搜索当前地区的位置
          outPutDirAuto: true,
          // type: '汽车服务|汽车销售|汽车维修|摩托车服务|餐饮服务|购物服务|生活服务|体育休闲服务|医疗保健服务|住宿服务|风景名胜|商务住宅|政府机构及社会团体|科教文化服务|交通设施服务|金融保险服务|公司企业|道路附属设施|地名地址信息|公共设施'
        });
      });
      // 创建搜索实例
      window.AMap.plugin('AMap.PlaceSearch', () => {
        placeSearch = new window.AMap.PlaceSearch({
          input: 'tipinputs',
          pageSize: 10,
          pageIndex: 1,
          // citylimit: true, // 仅搜索本城市的地名
        });
      });

      window.AMap.event.addListener(auto, 'select', (e) => {
        placeSearch.search(e.poi.name);
      });
    },
  };

  getQrcode = () => {
    this.props.dispatch({
      type: 'myModel/getSetData',
      payload: {
        id: this.state.id,
      },
      url: `/api/admin/stadium/quick/code`,
      method: 'GET',
      myData: (res) => {
        if (res && res.code === 200) {
          window.open(
            window.location.hostname == 'admin.sshtyt.cn'
              ? 'https://api.sshtyt.cn' + res.data
              : 'http://39.108.167.61:8006' + res.data,
            '_blank',
          );
          // let xhr = new XMLHttpRequest()
          // let fileName = `` // 文件名称
          // xhr.open('GET',res.data, true)
          // xhr.responseType = 'blob'
          // xhr.onload = function () {
          //     if (this.status === 200) {
          //         let type = xhr.getResponseHeader('Content-Type')

          //         let blob = new Blob([this.response], { type: type })
          //         if (typeof window.navigator.msSaveBlob !== 'undefined') {
          //             window.navigator.msSaveBlob(blob, fileName)
          //         } else {
          //             let URL = window.URL || window.webkitURL
          //             let objectUrl = URL.createObjectURL(blob)
          //             if (fileName) {
          //                 var a = document.createElement('a')
          //                 // safari doesn't support this yet
          //                 if (typeof a.download === 'undefined') {
          //                     window.location = objectUrl
          //                 } else {
          //                     a.href = objectUrl
          //                     a.download = fileName
          //                     document.body.appendChild(a)
          //                     a.click()
          //                     a.remove()
          //                 }
          //             } else {
          //                 window.location = objectUrl
          //             }
          //         }
          //     }
          // }
          // xhr.send()
        } else {
          message.error(res.message);
        }
      },
    });
  };

  addType = () => {
    this.props.dispatch({
      type: 'myModel/getSetData',
      payload: {
        categoryList: this.state.majorList,
      },
      url: `/ddql/business/shop/cate/update`,
      method: 'POST',
      myData: (res) => {
        if (res && res.code === 10000) {
          message.success(res.msg);
          this.setState({ addGroupModalVisible: false });
          this.getBusinessType();
        } else {
          message.error(res.msg);
          // this.setState({ isSelectForm: true });
        }
      },
    });
  };

  getBusinessType = () => {
    this.props.dispatch({
      type: 'myModel/getSetData',
      payload: {},
      url: `/ddql/business/shop/cate/get`,
      method: 'POST',
      myData: (res) => {
        if (res && res.code === 10000) {
          this.setState({ majorList: res.data });
        } else {
          message.error(res.msg);
        }
      },
    });
  };

  changeManager = async (item) => {
    // 区分 审核修改 和 编辑修改
    if (this.state.type == 'review') {
      // 审核修改
      return;
    }
    // 新增或修改管理员时，姓名和手机号都要填，且存在商家
    if (!this.state.id || !item.phone || !item.name) {
      return;
    }

    this.props.dispatch({
      type: 'myModel/getSetData',
      payload: {
        ...item,
        shopId: this.state.id,
        headManager: item.sort == 1 ? 1 : 0,
      },
      url: item?.id ? '/ddql/business/shop/manager/update' : '/ddql/business/shop/manager/add',
      method: 'POST',
      myData: (res) => {
        if (res && res.code == 10000) {
          message.success('操作成功');
          this.getManagerList();
        } else {
          message.error(res?.msg);
          this.getManagerList();
        }
      },
    });

  };

  review = (handleParam = {}) => {
    this.formRefs.current.validateFields().then((values) => {
      this.props.dispatch({
        type: 'myModel/getSetData',
        payload: {
          id: this.state.id,
          auditStatus: values.searchType,
          rejectReason: values.keyword,
          ...handleParam,
          managers: this.state.managerList.map((i, index) => ({
            ...i,
            headManager: index == 0 ? 1 : 0, // 是否是主管理员
          })),
          // customerCodeImg: this.state.thumbnailFileList.map((cz) => cz.response.data.url)[0],
          // sort: 1,
        },
        url: '/ddql/audit/handle',
        method: 'POST',
        myData: (res) => {
          if (res && res.code === 10000) {
            message.success(res.msg);
            this.setState({
              reviewVisible: false,
            });
            setTimeout(() => {
              history.goBack();
            }, 1000);
          } else {
            message.error(res.msg);
          }
        },
      });
    });
  };

  // 通用的文件变化处理
  handleUploadChange =
    (type) =>
      ({ file, fileList }) => {
        this.setState({ [type]: fileList }, () => {
          const { response = {} } = file;
          if (response.code == 10000) {
            const data = this.state[type];
            if (data[data.length - 1]) {
              data[data.length - 1].response.data.url =
                urlName + data[data.length - 1].response.data.url;
              this.setState({
                [type]: data,
              });
            }
          }
        });
      };

  // 通用的预览处理
  handlePreview = async (file) => {
    let src = file.url;
    if (!src) {
      src = await new Promise((resolve) => {
        const reader = new FileReader();
        reader.readAsDataURL(file.originFileObj);
        reader.onload = () => resolve(reader.result);
      });
    }
    this.setState({
      previewImage: src,
      previewOpen: true,
    });
  };

  // 关闭预览
  handleCancelPreview = () => {
    this.setState({ previewOpen: false });
  };

  addGroup = (typeList) => {
    const data = JSON.parse(JSON.stringify(this.state[typeList]));
    data.push({ sort: data.length + 1 });
    this.setState({ [typeList]: data });
  };

  moveType = () => {
    const data = structuredClone(this.state.majorList);
    this.props.dispatch({
      type: 'myModel/getSetData',
      payload: {
        searchIds: data.filter((i) => i.id).map((i) => i.id),
      },
      url: `/ddql/business/shop/cate/update/sort`,
      method: 'POST',
      myData: (res) => {
        if (res && res.code === 10000) {
          message.success(res.msg);
          this.getBusinessType();
        } else {
          message.error(res.msg);
        }
      },
    });
  };

  moveRow = (dragIndex, hoverIndex) => {
    console.log(dragIndex, hoverIndex);
    const { majorList } = this.state;
    const dragRow = majorList[dragIndex];
    // 判断移动是否有id
    this.setState(
      update(this.state, {
        majorList: {
          $splice: [
            [dragIndex, 1],
            [hoverIndex, 0, dragRow],
          ],
        },
      }),
    );
    if (dragRow?.id) {
      this.moveType();
    }
  };

  fastSelectTime = (index, months) => {
    // 表示自定义
    let topStartTime = undefined;
    let topEndTime = undefined;
    if (months) {
      topStartTime = dayjs();
      topEndTime = dayjs().add(months, 'month');
    }
    this.setState({
      topStartTime,
      topEndTime,
    });
    this.setState({
      fastIndex: index,
    });
  };

  onFinish = () => {
    this.formRef.current.validateFields().then((values) => {
      const params = {
        coverImageUrl: this.state.thumbnailFileList.map((cz) => cz.response.data.url)[0],
        galleryImages: this.state.carouselFileList.map((cz) => cz.response.data.url),
        status: values.status ? 1 : 0,
        remark: values.remark,
        name: values.name,
        location: this.state.locationStr,
        userName: values.userName ? values.userName : '',
        topRecommend: values.topRecommend ? 1 : 0,
        topConsumption: values.topConsumption ? 1 : 0,
        recommendOrder: values.recommendOrder ? values.recommendOrder : 0,
        phone: values.phone ? values.phone : '',
        address: this.state.locationValue,
        description: this.state.content,
        sortOrder: values.sortOrder,
        startTime: values.time ? values.time[0].format('HH:mm:00') : undefined,
        endTime: values.time ? values.time[1].format('HH:mm:00') : undefined,
        circleIds: values.circleIds,
        topStartTime: this.state.topStartTime
          ? this.state.topStartTime.format('YYYY-MM-DD 00:00:00')
          : undefined,
        topEndTime: this.state.topEndTime
          ? this.state.topEndTime.format('YYYY-MM-DD 23:59:59')
          : undefined,
        topConsumptionStartTime: values.topConsumptionStartTime
          ? values.topConsumptionStartTime.format('YYYY-MM-DD 00:00:00')
          : undefined,
        topConsumptionEndTime: values.topConsumptionEndTime
          ? values.topConsumptionEndTime.format('YYYY-MM-DD 23:59:59')
          : undefined,
        industryCategoryIds: values.industryCategoryIds,
        id: this.state.id || undefined,
      };

      // 判断如果是 review，则调review函数
      if (this.state.type == 'review') {
        this.review(params);
        return;
      }

      this.props.dispatch({
        type: 'myModel/getSetData',
        payload: {
          ...params,
        },
        url: `/ddql/business/shop/save`,
        method: 'POST',
        myData: (res) => {
          if (res && res.code === 10000) {
            message.success(res.msg);
            // 新增时，添加管理员
            if (this.state.type == 'add') {
              this.props.dispatch({
                type: 'myModel/getSetData',
                payload: {
                  sort: 1,
                  shopId: res.data,
                  name: values.userName,
                  phone: values.phone,
                  headManager: 1,
                },
                url: `/ddql/business/shop/manager/add`,
                method: 'POST',
                myData: (ress) => {
                  history.goBack()
                }
              })
              return;
            }
            this.getData();
            this.getManagerList();
            this.props.editShopName(values.name);
          } else {
            message.error(res.msg);
          }
        },
      });
    });
  };

  showReview = () => {
    this.setState({ reviewVisible: true });
  };

  render() {
    const { thumbnailFileList, carouselFileList, previewImage, previewOpen, type, disabled } =
      this.state;
    const typeColumns = [
      {
        title: '序号',
        render: (res, record, index) => <div>{index + 1}</div>,
      },
      {
        title: '行业类别名称',
        dataIndex: 'name',
        render: (res, record, index) => (
          <div>
            <Input
              onChange={(e) => {
                const data = JSON.parse(JSON.stringify(this.state.majorList));
                data[index].name = e.target.value;
                this.setState({
                  majorList: data,
                });
              }}
              value={record.name}
              placeholder="请输入"
            />
          </div>
        ),
      },
      {
        title: '操作',
        render: (res, record, index) => (
          <Popconfirm
            title={
              <>
                <div>删除提示</div>
                <div>
                  <span style={{ color: 'red' }}>删除的内容不可恢复</span>，
                  <span style={{ color: '#ccc' }}>确定删除吗？</span>
                </div>
              </>
            }
            onConfirm={() => {
              if (record.id) {
                this.props.dispatch({
                  type: 'myModel/getSetData',
                  payload: {
                    searchIds: [record.id],
                  },
                  url: `/ddql/business/shop/cate/del`,
                  method: 'POST',
                  myData: (res) => {
                    if (res && res.code === 10000) {
                      message.success(res.msg);
                      this.getBusinessType();
                    } else {
                      message.error(res.msg);
                    }
                  },
                });
              } else {
                const data = JSON.parse(JSON.stringify(this.state.majorList));
                data.splice(index, 1);
                data.map((resd, index) => {
                  resd.sort = index + 1;
                });
                this.setState({
                  majorList: data,
                });
              }
            }}
            // onCancel={cancel}
            okText="是"
            cancelText="否"
          >
            <span className="mL15 red">删除</span>
          </Popconfirm>
        ),
      },
    ];
    const managerColumns = [
      {
        title: '序号',
        render: (res, record, index) => <div>{index + 1}</div>,
      },
      {
        title: '姓名',
        dataIndex: 'name',
        render: (res, record, index) => (
          <div>
            <Input
              onChange={(e) => {
                const data = JSON.parse(JSON.stringify(this.state.managerList));
                data[index].name = e.target.value;
                this.setState({
                  managerList: data,
                });
              }}
              onBlur={(e) => this.changeManager(record)}
              value={record.name}
              placeholder="请输入"
            />
          </div>
        ),
      },
      {
        title: '手机号',
        dataIndex: 'phone',
        render: (res, record, index) => (
          <div>
            <Input
              onChange={(e) => {
                const data = JSON.parse(JSON.stringify(this.state.managerList));
                data[index].phone = e.target.value;
                this.setState({
                  managerList: data,
                });
              }}
              onBlur={(e) => this.changeManager(record)}
              onClick={(e) => {
                // 判断是否是修改店长手机号且店长已存在
                if (!index && record.id) {
                  e.preventDefault();
                  e.target.blur();
                  Modal.confirm({
                    content:
                      '编辑店长手机号，那么此商家的权限就会变更到新手机号的用户小程序上，是否确定继续操作？',
                    centered: true,
                    onOk: () => {
                      e.target.focus();
                    },
                  });
                }
              }}
              value={record.phone}
              placeholder="请输入"
            />
          </div>
        ),
      },
      {
        title: '操作',
        render: (res, record, index) =>
          index != 0 && (
            <Popconfirm
              title={
                <>
                  <div>删除提示</div>
                  <div>
                    <span style={{ color: 'red' }}>删除的内容不可恢复</span>，
                    <span style={{ color: '#ccc' }}>确定删除吗？</span>
                  </div>
                </>
              }
              onConfirm={async () => {
                if (record.id && this.state.type != 'review') {
                  this.props.dispatch({
                    type: 'myModel/getSetData',
                    payload: {
                      deleteIds: [record.id],
                    },
                    url: `/ddql/business/shop/manager/del`,
                    method: 'POST',
                    myData: (res) => {
                      if (res && res.code == 10000) {
                        message.success(res.msg);
                        this.getManagerList();
                      } else {
                        message.error(res.msg);
                      }
                    }
                  })
                } else {
                  const data = JSON.parse(
                    JSON.stringify(this.state.managerList),
                  );
                  data.splice(index, 1);
                  data.map((resd, index) => {
                    resd.sort = index + 1;
                  });
                  this.setState({
                    managerList: data,
                  });
                }
              }}
              // onCancel={cancel}
              okText="是"
              cancelText="否"
            >
              <Button icon={<MinusCircleOutlined />} color="danger" variant="text" />
            </Popconfirm>
          ),
      },
    ];
    const uploadProps = (type, index) => {
      return {
        name: 'file',
        action: '/ddql/file/upload',

        onChange: (info) => {
          const fileType = [
            'doc',
            'txt',
            'pdf',
            'zip',
            'rar',
            'xls',
            'xlsx',
            'docs',
            'pptx',
            'ppt',
          ];
          if (info.file.status === 'done') {
            console.log(info.file);
            const url = info.file.response.data.uri;
            const { ckeditor } = this;
            const ele = ckeditor.instance.document.createElement('img');
            ele.setAttribute('src', url);

            ckeditor.instance.insertElement(ele);
            // }
          }
        },
      };
    };

    const props = {
      grid: false,
      width: 690,
      height: 312,
      resize: true,
      resizeAndDrag: true,
      modalTitle: '上传图片',
      modalWidth: 600,
    };

    const uploadButton = (
      <button style={{ border: 0, background: 'none' }} type="button">
        <PlusOutlined />
        <div style={{ marginTop: 8 }}>上传</div>
      </button>
    );
    // 图片格式校验（限制为jpg/jpeg/png）
    const beforeUpload = (file) => {
      const isJpgOrPng = file.type === 'image/jpeg' || file.type === 'image/png';
      if (!isJpgOrPng) {
        message.error('仅支持上传 JPG/PNG 格式的图片！');
        return false;
      }
      return true;
    };

    return (
      <Spin spinning={this.state.spinning}>
        <Row align="middle" justify="space-between">
          <Col span={4}>
            <h2>商家信息</h2>
          </Col>
          <Col span={2}>
            <Button onClick={() => history.goBack()}>返回</Button>
          </Col>
        </Row>
        <Form
          ref={this.formRef}
          labelCol={{
            span: 2,
          }}
          wrapperCol={{
            span: 10,
          }}
          initialValues={{
            remember: true,
          }}
          onFinish={this.onFinish}
          autoComplete="off"
          disabled={disabled}
        >
          {/* 缩略图上传 */}
          <Form.Item
            label={
              <span>
                <span style={{ color: 'red' }}>*</span>商家封面图
              </span>
            }
          >
            <Form.Item noStyle>
              <ImgCrop {...props}>
                <Upload
                  disabled={disabled}
                  action="/ddql/file/upload"
                  listType="picture-card"
                  fileList={thumbnailFileList}
                  onChange={this.handleUploadChange('thumbnailFileList')}
                  onPreview={this.handlePreview}
                  beforeUpload={beforeUpload}
                  accept="image/jpeg,image/png"
                  headers={{ token: localStorage.getItem('token') }}
                >
                  {thumbnailFileList.length < 1 && uploadButton}
                </Upload>
              </ImgCrop>
            </Form.Item>
            <span style={{ color: '#ccc' }}>支持在线裁剪尺寸，图片格式支持jpg/jpeg/png</span>
          </Form.Item>

          {/* 轮播图上传 */}
          <Form.Item
            label={
              <span>
                <span style={{ color: 'red' }}>*</span>商家轮播图
              </span>
            }
          >
            <Form.Item noStyle>
              <ImgCrop {...props}>
                <Upload
                  disabled={disabled}
                  action="/ddql/file/upload"
                  listType="picture-card"
                  fileList={carouselFileList}
                  onChange={this.handleUploadChange('carouselFileList')}
                  onPreview={this.handlePreview}
                  beforeUpload={beforeUpload}
                  accept="image/jpeg,image/png"
                  headers={{ token: localStorage.getItem('token') }}
                >
                  {carouselFileList.length < 5 && uploadButton}
                </Upload>
              </ImgCrop>
            </Form.Item>
            <span style={{ color: '#ccc' }}>
              支持上传最多五张图，支持在线裁剪尺寸，图片格式支持jpg/jpeg/png
            </span>
          </Form.Item>

          {/* 预览模态框 */}
          <Modal open={previewOpen} footer={null} onCancel={this.handleCancelPreview}>
            <img alt="预览" style={{ width: '100%' }} src={previewImage} />
          </Modal>
          <Form.Item label="商家名称" name="name" rules={[{ required: true, message: '请输入!' }]}>
            <Input placeholder="请输入" />
          </Form.Item>

          <Form.Item
            label={
              <div>
                <span style={{ color: 'red', paddingRight: 4 }}>*</span>商家地址
              </div>
            }
            name="location"
            rules={[{ required: false, message: '请选择!' }]}
          >
            <div style={{ display: 'flex', width: '100%' }}>
              <Input
                disabled
                value={this.state.locationValue}
                placeholder="请选择位置"
                style={{ marginRight: 8 }}
              ></Input>
              <Button type="default" className="darkBlue-btn" onClick={this.showModal}>
                选择位置
              </Button>
            </div>
          </Form.Item>
          <Form.Item
            label="联系人"
            name="userName"
            rules={[{ required: false, message: '请选择!' }]}
          >
            <Input
              placeholder="请输入"
              onChange={(e) => {
                if (type != 'add') return;
                const data = structuredClone(this.state.managerList);
                const value = e.target.value;
                // 存在商家管理人员，直接修改
                if (data.length > 0) {
                  data[0].name = value;
                  this.setState({ managerList: data });
                } else {
                  // 没有商家管理人员，新增一个
                  this.setState({ managerList: [{ name: value, sort: 1 }] });
                }
              }}
            />
          </Form.Item>
          <Form.Item
            label="联系电话"
            name="phone"
            rules={[{ required: false, message: '请选择!' }]}
          >
            <Input
              placeholder="请输入"
              onChange={(e) => {
                if (type != 'add') return;
                const data = structuredClone(this.state.managerList);
                const value = e.target.value;
                // 存在商家管理人员，直接修改
                if (data.length > 0) {
                  data[0].phone = value;
                  this.setState({ managerList: data });
                } else {
                  // 没有商家管理人员，新增一个
                  this.setState({ managerList: [{ phone: value, sort: 1 }] });
                }
              }}
            />
          </Form.Item>
          <Form.Item label="营业时间" name="time" rules={[{ required: false, message: '请选择!' }]}>
            <TimePicker.RangePicker format="HH:mm" />
          </Form.Item>
          <Form.Item
            label={
              <div>
                <span style={{ color: 'red' }}>*</span>行业类别
              </div>
            }
            rules={[{ required: true, message: '请选择!' }]}
          >
            <div style={{ display: 'flex', width: '100%' }}>
              <Form.Item
                name="industryCategoryIds"
                noStyle
                rules={[{ required: true, message: '请选择!' }]}
              >
                <Select
                  mode="multiple"
                  className="norBorder"
                  placeholder="请选择"
                  style={{ flex: 1 }}
                >
                  {this.state.majorList.map((sa) => (
                    <Option key={sa.id} value={sa.id}>
                      {sa.name}
                    </Option>
                  ))}
                </Select>
              </Form.Item>
              <Button
                className="nolBorder"
                type="primary"
                onClick={() => this.setState({ addGroupModalVisible: true })}
              >
                新增分类
              </Button>
            </div>
          </Form.Item>
          <Form.Item
            label="所属商圈"
            name="circleIds"
            rules={[{ required: false, message: '请选择!' }]}
          >
            <Select mode="multiple" placeholder="请选择" showSearch optionFilterProp="children">
              {this.props.circleList.map((sxsa) => (
                <Option value={sxsa.id}>{sxsa.name}</Option>
              ))}
            </Select>
          </Form.Item>
          <Form.Item
            label={
              <div>
                <span style={{ color: 'red' }}>*</span>商家管理人员
              </div>
            }
            rules={[{ required: true, message: '请选择!' }]}
          >
            <Table
              columns={managerColumns}
              dataSource={this.state.managerList}
              pagination={false}
              rowKey="id"
            />
            <Button
              onClick={() => this.addGroup('managerList')}
              type="dashed"
              disabled={disabled || this.state.managerList.length > 3}
              style={{ marginTop: 20, width: '100%' }}
            >
              + 添加
            </Button>
          </Form.Item>
          <Form.Item
            label="置顶推荐"
            name="topRecommend"
            rules={[{ required: false, message: '请选择!' }]}
          >
            <Radio.Group
              onChange={(e) => {
                this.setState({ topRecommend: e.target.value });
              }}
            >
              <Radio value={1}>是</Radio>
              <Radio value={0}>否</Radio>
            </Radio.Group>
          </Form.Item>
          {this.state.topRecommend == 1 && (
            <Form.Item label="快速选择" rules={[{ required: false, message: '请选择!' }]}>
              <div style={{ display: 'flex', alignItems: 'center', gap: 15 }}>
                {this.state.fastSelectList.map((i, index) => (
                  <div
                    style={{
                      backgroundColor: this.state.fastIndex == index ? '#1890ff' : '#f2f2f2',
                      color: this.state.fastIndex == index ? '#fff' : '#333',
                      borderRadius: 18,
                      cursor: 'pointer',
                      padding: '2px 20px',
                    }}
                    onClick={() => !disabled && this.fastSelectTime(index, i.value)}
                  >
                    {i.title}
                  </div>
                ))}
              </div>
            </Form.Item>
          )}
          {this.state.topRecommend == 1 && this.state.fastIndex == 4 && (
            <Form.Item
              label="置顶开始日期"
              // name="topStartTime"
              rules={[{ required: false, message: '请选择!' }]}
            >
              <DatePicker
                value={this.state.topStartTime}
                onChange={(e) => {
                  this.setState({
                    topStartTime: dayjs(e),
                  });
                }}
              />
            </Form.Item>
          )}
          {this.state.topRecommend == 1 && this.state.fastIndex == 4 && (
            <Form.Item
              label="置顶结束日期"
              // name="topEndTime"
              rules={[{ required: false, message: '请选择!' }]}
            >
              <DatePicker
                value={this.state.topEndTime}
                onChange={(e) => {
                  this.setState({
                    topEndTime: dayjs(e),
                  });
                }}
              />
            </Form.Item>
          )}
          {this.state.topRecommend == 1 && (
            <Form.Item
              label={
                <span>
                  <span style={{ color: 'red' }}>*</span>推荐顺序
                </span>
              }
            >
              <Form.Item
                noStyle
                name="recommendOrder"
                rules={[{ required: true, message: '请输入' }]}
              >
                <InputNumber placeholder="请输入" />
              </Form.Item>
              <div style={{ color: '#ccc' }}>数值越大，推荐顺序越前</div>
            </Form.Item>
          )}
          <Form.Item
            label="启用状态"
            name="status"
            rules={[{ required: true, message: '请选择!' }]}
            valuePropName="checked"
            initialValue={true}
          >
            <Switch checkedChildren="开启" unCheckedChildren="关闭" />
          </Form.Item>
          <Form.Item
            label="消费置顶"
            name="topConsumption"
            rules={[{ required: true, message: '请选择!' }]}
            valuePropName="checked"
            initialValue={false}
          >
            <Switch
              checkedChildren="开启"
              unCheckedChildren="关闭"
              onChange={(e) => this.setState({ topConsumption: e ? 1 : 0 })}
            />
          </Form.Item>
          {this.state.topConsumption == 1 && (
            <Form.Item
              label="消费置顶开始日期"
              name="topConsumptionStartTime"
              rules={[{ required: false, message: '请选择!' }]}
            >
              <DatePicker />
            </Form.Item>
          )}
          {this.state.topConsumption == 1 && (
            <Form.Item
              label="消费置顶结束日期"
              name="topConsumptionEndTime"
              rules={[{ required: false, message: '请选择!' }]}
            >
              <DatePicker />
            </Form.Item>
          )}
          <Form.Item label={<span>商家介绍</span>} rules={[{ required: true, message: '请输入!' }]}>
            <div style={{ position: 'relative', marginTop: '-15px', width: 600 }}>
              <Upload
                showUploadList={false}
                accept={'image/*'}
                // headers={{
                //   Authorization: getToken()
                //
                {...uploadProps(1)}
              >
                <div className="zxc" />
              </Upload>
              <CKEditor
                ref={(ckeditor) => {
                  this.ckeditor = ckeditor;
                }}
                value={this.state.content}
                config={{
                  toolbar: [
                    {
                      name: 'clipboard',
                      items: ['Cut', 'Copy', 'Paste', 'PasteText', 'PasteFromWord', '-'],
                    },
                    {
                      name: 'basicstyles',
                      items: ['Bold', 'Italic', 'Underline', '-', 'CopyFormatting'],
                    },
                    {
                      name: 'paragraph',
                      items: [
                        'NumberedList',
                        'BulletedList',
                        '-',
                        'Outdent',
                        'Indent',
                        '-',
                        'JustifyLeft',
                        'JustifyCenter',
                        'JustifyRight',
                        'JustifyBlock',
                        '-',
                      ],
                    },
                    { name: 'links', items: ['Link', 'Unlink'] },
                    { name: 'insert', items: ['Image', 'Table'] },
                    { name: 'styles', items: ['Font', 'FontSize'] },
                    { name: 'colors', items: ['TextColor', 'BGColor'] },
                    { name: 'tools', items: ['Maximize'] },
                  ],
                  extraPlugins: 'placeholder',
                  height: 250,
                  uploadUrl: '/home/media/upload',
                  removeDialogTabs: 'image:advanced;link:advanced',
                }}
                onChange={this.updateContent}
              />
            </div>
          </Form.Item>
          <Form.Item label="备注" name="remark">
            <TextArea rows={4} placeholder="请输入" />
          </Form.Item>
          <Form.Item>
            {(type == 'add' || type == 'edit') && (
              <>
                <Button
                  onClick={() => {
                    history.goBack()
                  }}
                >
                  取消
                </Button>
                <Button className="mL15" type="primary" htmlType="submit">
                  保存
                </Button>
              </>
            )}
            {type == 'review' && (
              <Button className="mL15" type="primary" onClick={this.showReview}>
                审核
              </Button>
            )}
          </Form.Item>
        </Form>
        {this.state.map && (
          <Modal
            title="选择位置"
            visible
            onOk={this.handleOkz}
            zIndex={102}
            onCancel={this.handleCancelz}
            style={{ paddingBottom: 50, minWidth: 800 }}
          >
            <div style={{ width: '100%', marginBottom: 50 }}>
              <Map
                locationName={this.state.locationStr}
                locationInfo={this.state.locationInfo}
                isAdd={this.props.isAdd}
                onLocationChange={(locationInfo) => {
                  console.log('收到地图数据:', locationInfo);
                  const locationStr =
                    locationInfo && locationInfo.location.lat && locationInfo.location.lng
                      ? `${locationInfo.location.lng},${locationInfo.location.lat}`
                      : '';
                  console.log('收到地图数据:', locationStr);
                  this.setState({
                    locationInfo, // 存储地图数据
                    locationStr,
                  });
                }}
              />
            </div>
          </Modal>
        )}
        <Modal
          style={{ minWidth: '50%' }}
          open={this.state.addGroupModalVisible}
          onCancel={() =>
            this.setState({
              addGroupModalVisible: false,
              majorList: this.state.majorList.filter((i) => i.id),
            })
          }
          title="新增行业类别"
          zIndex={2000}
          onOk={this.addType}
        >
          <Alert message="按住鼠标拖拽可调整展示顺序" showIcon style={{ textAlign: 'left' }} />
          <div className="modal-wrapper">
            <DndProvider backend={HTML5Backend}>
              <Table
                defaultSize="large"
                style={{ paddingTop: 25 }}
                columns={typeColumns}
                dataSource={this.state.majorList}
                search={false}
                options={false}
                pagination={false}
                components={this.components}
                scroll={{ y: 630 }}
                onRow={(record, index) => ({
                  index,
                  moveRow: this.moveRow,
                })}
              />
            </DndProvider>
            <Button
              onClick={() => this.addGroup('majorList')}
              type="dashed"
              style={{ marginTop: 20, width: '100%' }}
            >
              + 添加行业类别
            </Button>
          </div>
        </Modal>
        <Modal
          visible={this.state.reviewVisible}
          title="审核"
          onOk={this.onFinish}
          onCancel={() => {
            this.formRefs.current.resetFields();
            this.setState({
              searchType: 1,
              reviewVisible: false,
            });
          }}
        >
          <Form
            ref={this.formRefs}
            labelCol={{ span: 6 }}
            wrapperCol={{ span: 18 }}
            initialValues={{
              remember: true,
            }}
            autoComplete="off"
          >
            <Form.Item
              label="审核结果"
              name="searchType"
              rules={[{ required: true, message: '请输入！' }]}
              initialValue={1}
            >
              <Radio.Group
                onChange={(e) => {
                  this.setState({
                    searchType: e.target.value,
                  });
                }}
              >
                <Radio value={1}>通过</Radio>
                <Radio value={2}>驳回</Radio>
              </Radio.Group>
            </Form.Item>
            {this.state.searchType == 2 && (
              <Form.Item
                label="驳回原因"
                name="keyword"
                rules={[{ required: true, message: '请输入！' }]}
              >
                <TextArea placeholder="请输入"></TextArea>
              </Form.Item>
            )}
          </Form>
        </Modal>
      </Spin>
    );
  }
}
export default connect()(AddShopMsgModal);
