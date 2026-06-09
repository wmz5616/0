import React from 'react';
import {
  Modal,
  Form,
  Input,
  Select,
  Radio,
  DatePicker,
  Upload,
  message,
  InputNumber,
  Alert,
  Table,
  Button,
} from 'antd';
import { LoadingOutlined, PlusOutlined } from '@ant-design/icons';
import { history, connect, Link } from 'umi';
import moment from 'moment';
import CKEditor from 'react-ckeditor-wrapper';
import { DndProvider, DragSource, DropTarget } from 'react-dnd';
import { HTML5Backend } from 'react-dnd-html5-backend';
import update from 'immutability-helper';
import ImgCrop from 'antd-img-crop';
const { Option } = Select;
const { RangePicker } = DatePicker;
import { urlName } from '@/utils/utils';
import { getToken } from '@/utils/authority';
import dayjs from 'dayjs';
import customParseFormat from 'dayjs/plugin/customParseFormat';
dayjs.extend(customParseFormat);
const layout = {
  labelCol: { span: 5 },
  wrapperCol: { span: 17 },
};

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
class App extends React.Component {
  formRef = React.createRef();
  state = {
    pois: [],
    loading: false,
    xxxx: true,
    scheduling: 1,
    majorList: [],
    gymList: [],
    carouselFileList: [],
    thumbnailFileList: [],
  };

  components = {
    body: {
      row: DragableBodyRow,
    },
  };

  componentDidMount() {
    this.getGymList();
  }

  getGymList = () => {
    //获取商品专业列表
    this.props.dispatch({
      type: 'myModel/getSetData',
      payload: {
        searchId: this.props.id,
      },
      url: `/ddql/equipmentPoster/select`,
      method: 'POST',
      myData: (res) => {
        if (res && res.code === 10000) {
          this.setState({
            majorList: res.data,
          });
        } else {
          message.error(res.msg);
          // this.setState({ isSelectForm: true });
        }
      },
    });
  };

  addGroup = () => {
    const data = JSON.parse(JSON.stringify(this.state.majorList));
    data.push({ sort: data.length + 1, equipmentId: this.props.id });
    this.setState({ majorList: data });
  };

  handleOk = () => {
    this.formRef.current.validateFields().then((values) => {
      const params = {
        coverImage: this.state.thumbnailFileList.map((cz) => cz.response.data.url)[0],
        galleryImages: this.state.carouselFileList.map((cz) => cz.response.data.url),
        status: values.status,
        remark: values.remark,
        name: values.name,
        detail: this.state.content,
        remark: values.remark,
        isVirtual: values.isVirtual,
        categoryIds: values.categoryIds.join(','),
        sort: values.sort,
        exchangeAmount: values.exchangeAmount,
        specification: values.specification,
        unit: values.unit,
        scheduledTime:
          values.status == 3 ? values.scheduledTime.format('YYYY-MM-DD HH:mm:ss') : undefined,
      };
      if (!this.props.add) {
        params.id = this.state.id;
      }
      this.props.dispatch({
        type: 'myModel/getSetData',
        payload: {
          ...params,
        },
        url: `/ddql/product/save`,
        method: 'POST',
        myData: (res) => {
          if (res && res.code === 10000) {
            message.success(res.msg);
            this.props.getData();
            this.props.handleOk();
          } else {
            message.error(res.msg);
            // this.setState({ isSelectForm: true });
          }
        },
      });
    });
  };

  handleCancel = () => {
    const { handleOk } = this.props;
    handleOk();
  };

  onChange = (value) => {
    this.setState({
      scheduling: value,
    });
  };

  addCategory = () => {
    if (
      this.state.majorList.filter(
        (x) => !x.showTime || !x.showBeginTime || !x.showEndTime || !x.image,
      ).length != 0
    ) {
      message.info('请填写完整在提交');
      return;
    }
    this.props.dispatch({
      type: 'myModel/getSetData',
      payload: {
        data: this.state.majorList,
      },
      url: `/ddql/equipmentPoster/save`,
      method: 'POST',
      myData: (res) => {
        if (res && res.code === 10000) {
          message.success(res.msg);
          this.props.cancelModal();
        } else {
          message.error(res.msg);
          // this.setState({ isSelectForm: true });
        }
      },
    });
  };

  moveRow = (dragIndex, hoverIndex) => {
    console.log(dragIndex, hoverIndex);
    const { majorList } = this.state;
    const dragRow = majorList[dragIndex];
    console.log(hoverIndex);

    this.setState(
      update(this.state, {
        majorList: {
          $splice: [
            [dragIndex, 1],
            [hoverIndex, 0, dragRow],
          ],
        },
      }),
      () => {
        this.props.dispatch({
          type: 'myModel/getSetData',
          payload: {
            searchIds: this.state.majorList.map((xz) => xz.id),
          },
          url: `/ddql/equipmentPoster/sort/set`,
          method: 'POST',
          myData: (res) => {
            if (res && res.code === 10000) {
              message.success(res.msg);
              this.getGymList();
            } else {
              message.error(res.msg);
              // this.setState({ isSelectForm: true });
            }
          },
        });
      },
    );
  };

  beforeUpload = (file) => {
    const isJpgOrPng = file.name.indexOf('jfif') == -1;
    console.log(isJpgOrPng);
    if (!isJpgOrPng) {
      message.error('不符合类型文件');
    }

    return isJpgOrPng && true;
  };

  render() {
    const uploadButton = (
      <div>
        {loading ? <LoadingOutlined /> : <PlusOutlined />}
        <div style={{ marginTop: 8 }}>上传</div>
      </div>
    );
    const typeColumns = [
      {
        title: '图片',
        render: (res, record, index) => (
          <Upload
            name="file"
            listType="picture-card"
            className="avatar-uploader"
            showUploadList={false}
            action="/ddql/file/upload"
            headers={{ token: getToken() }}
            beforeUpload={this.beforeUpload}
            onChange={(info) => {
              if (info.file.status === 'uploading') {
                this.setState({ loading: true });
                return;
              }
              if (info.file.status === 'done') {
                const { response = {} } = info.file;
                const data = JSON.parse(JSON.stringify(this.state.majorList));
                data[index].image = urlName + response.data.url;
                this.setState({
                  majorList: data,
                });

                message.success({ content: '上传成功', duration: 0.7 });
                // Get this url from response in real world
              }
            }}
          >
            {record.image ? (
              <img
                src={record.image}
                alt="avatar"
                style={{ width: '100%', height: '100%', objectFit: 'cover' }}
              />
            ) : (
              uploadButton
            )}
          </Upload>
        ),
      },
      {
        title: '投放周期',
        dataIndex: 'name',
        width: 320,
        render: (res, record, index) => (
          <div>
            <RangePicker
              style={{ width: '100%' }}
              value={
                record.showBeginTime
                  ? [dayjs(record.showBeginTime), dayjs(record.showEndTime)]
                  : undefined
              }
              showTime
              onChange={(e, a) => {
                const data = JSON.parse(JSON.stringify(this.state.majorList));
                data[index].showBeginTime = a[0];
                data[index].showEndTime = a[1];
                this.setState({
                  majorList: data,
                });
              }}
              placeholder="请输入"
            />
          </div>
        ),
      },
      {
        title: '展示时长/秒',
        dataIndex: 'showTime',
        render: (res, record, index) => (
          <div>
            <InputNumber
              value={record.showTime}
              onChange={(e) => {
                const data = JSON.parse(JSON.stringify(this.state.majorList));
                data[index].showTime = e;
                this.setState({
                  majorList: data,
                });
              }}
              placeholder="请输入"
            />
          </div>
        ),
      },
      {
        title: '操作',
        render: (res, record, index) => (
          <div>
            {/* <span
              className="clickFont"
              onClick={() => {
                const params = {
                  name: record.name,
                  sort: record.sort,
                };
                if (record.id) {
                  params.id = record.id;
                }
                this.props.dispatch({
                  type: 'myModel/getSetData',
                  payload: {
                    ...params,
                  },
                  url: record.id ? `/api/admin/course/major/update` : `/api/admin/course/major/add`,
                  method: 'POST',
                  myData: (res) => {
                    if (res && res.code === 200) {
                      message.success(res.message);
                      this.getGymList();
                    } else {
                      message.error(res.message);
                      // this.setState({ isSelectForm: true });
                    }
                  },
                });
              }}
            >
              保存
            </span> */}
            <span
              className="mL15 red"
              onClick={() => {
                if (record.id) {
                  this.props.dispatch({
                    type: 'myModel/getSetData',
                    payload: {
                      deleteIds: [record.id],
                    },
                    url: `/ddql/equipmentPoster/delete`,
                    method: 'POST',
                    myData: (res) => {
                      if (res && res.code === 10000) {
                        message.success(res.msg);
                        this.getGymList();
                      } else {
                        message.error(res.msg);
                        // this.setState({ isSelectForm: true });
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
            >
              删除
            </span>
          </div>
        ),
      },
    ];
    const { add } = this.props;

    const { imageUrl, loading } = this.state;

    return (
      <>
        <Modal
          style={{ minWidth: '60%' }}
          open
          onCancel={() => this.props.cancelModal()}
          title="海报设置"
          zIndex={2000}
          onOk={this.addCategory}
        >
          <Alert
            message="按住鼠标拖拽可调整展示顺序"
            showIcon
            style={{ textAlign: 'left' }}
            type="warning"
          />
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
            <Button onClick={this.addGroup} type="dashed" style={{ marginTop: 20, width: '100%' }}>
              + 添加
            </Button>
          </div>
        </Modal>
      </>
    );
  }
}
export default connect()(App);
