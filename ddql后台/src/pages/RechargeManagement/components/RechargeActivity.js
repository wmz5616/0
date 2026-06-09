import React from 'react';
import { PageContainer } from '@ant-design/pro-layout';
import {
  Button,
  Select,
  Input,
  Table,
  Row,
  Col,
  Form,
  message,
  Popconfirm,
  Spin,
  Alert,
  Modal,
  Upload,
  Radio,
  InputNumber,
  Switch,
} from 'antd';
import { DndProvider, DragSource, DropTarget } from 'react-dnd';
import { HTML5Backend } from 'react-dnd-html5-backend';
import update from 'immutability-helper';
import { getToken } from '@/utils/authority';
import { LoadingOutlined, PlusOutlined } from '@ant-design/icons';

import AddRecharge from './AddRecharge';

// 将connect导入
import { connect } from 'umi';

// 应用类型
const { Option } = Select;
const { Search } = Input;
const { TextArea } = Input;

const layout = {
  labelCol: { span: 6 },
  wrapperCol: { span: 16 },
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

class DataConnection extends React.Component {
  formRef = React.createRef();
  formRefs = React.createRef();
  formRefss = React.createRef();
  state = {
    NewType: false,
    RecommendedSettings: false,
    confirmLoading: false,
    spinning: false,
    loading: false,
    imageUrl: '',
    gymLists: [],
  };

  componentDidMount() {
    this.getData();
  }

  getData = () => {
    this.setState(
      {
        spinning: true,
      },
      () => {
        //列表
        this.props.dispatch({
          type: 'myModel/getSetData',
          payload: {},
          url: `/ddql/recharge/activity/lists`,
          method: 'POST',
          myData: (res) => {
            this.setState({
              spinning: false,
            });
            if (res && res.code === 10000) {
              this.setState({
                gymTypelist: res.data,
              });
            } else {
              message.error(res.message);
              // this.setState({ isSelectForm: true });
            }
          },
        });

        //重置配置详情
        // this.props.dispatch({
        //   type: 'myModel/getSetData',
        //   payload: {},
        //   url: `/api/admin/top_up/config/info`,
        //   method: 'GET',
        //   myData: (res) => {
        //     if (res && res.code === 200) {
        //       this.setState({
        //         useStatus: res.data.use_status, //是否开启充值活动指定使用范围：0否，1是
        //       });
        //     }
        //   },
        // });
      },
    );
  };

  components = {
    body: {
      row: DragableBodyRow,
    },
  };

  moveRow = (dragIndex, hoverIndex) => {
    console.log(dragIndex, hoverIndex);
    const { gymTypelist } = this.state;
    const dragRow = gymTypelist[dragIndex];
    console.log(hoverIndex);

    this.setState(
      update(this.state, {
        gymTypelist: {
          $splice: [
            [dragIndex, 1],
            [hoverIndex, 0, dragRow],
          ],
        },
      }),
      () => {
        this.setState(
          {
            updataCloneList: this.state.gymTypelist,
          },
          () => {
            const reverseData = [];
            for (let i = 0; i < this.state.updataCloneList.length; i += 1) {
              reverseData.push(i + 1);
            }
            reverseData.reverse();
            console.log(reverseData);
            const cloneDeep = [];
            for (let i = 0; i < this.state.updataCloneList.length; i += 1) {
              console.l;
              cloneDeep.push({
                id: this.state.updataCloneList[i].id,
                sort: reverseData[i],
              });
            }
            console.log(cloneDeep);
            this.props.dispatch({
              type: 'myModel/getSetData',
              payload: {
                searchIds: cloneDeep.map((res) => res.id),
              },
              url: `/ddql/recharge/activity/sort`,
              method: 'POST',
              myData: (res) => {
                if (res && res.code === 10000) {
                  message.success(res.msg);
                  this.getData();
                } else {
                  message.error(res.msg);
                  // this.setState({ isSelectForm: true });
                }
              },
            });
          },
        );
      },
    );
  };

  showModal = (record) => {
    this.setState({
      AddRecharge: true,
      info: record,
    });
  };

  handleOk = () => {
    this.setState({
      AddRecharge: false,
    });
  };

  //删除
  deletes = (ids) => {
    if (ids == undefined) {
      message.error('请选择需要操作的活动');
    } else {
      this.props.dispatch({
        type: 'myModel/getSetData',
        payload: {
          deleteId: ids,
        },
        url: `/ddql/recharge/activity/delete`,
        method: 'POST',
        myData: (res) => {
          if (res && res.code === 10000) {
            message.success(res.msg);
            this.getData();
          } else {
            message.error(res.msg);
            // this.setState({ isSelectForm: true });
          }
        },
      });
    }
  };

  onSelectChange = (selectedRowKeys) => {
    //触发表单筛选
    this.setState({ selectedRowKeys });
  };

  submit = () => {
    if (this.state.gymTypelist.length == 0) {
      message.error('请添加充值活动');
      return;
    }
    this.props.dispatch({
      type: 'myModel/getSetData',
      payload: {
        rechargeActivityList: this.state.gymTypelist,
      },
      url: `/ddql/recharge/activity/save`,
      method: 'POST',
      myData: (res) => {
        if (res && res.code === 10000) {
          message.success(res.msg);
          this.getData();
        } else {
          message.error(res.msg);
          // this.setState({ isSelectForm: true });
        }
      },
    });
  };

  render() {
    const { gymTypelist = [], loading, selectedRowKeys } = this.state;

    const rowSelection = {
      selectedRowKeys,
      onChange: this.onSelectChange,
    };

    const columns = [
      {
        title: '充值金额',
        dataIndex: 'rechargeAmount',
        render: (text, record, index) => (
          <div>
            <InputNumber
              value={text/100}
              onChange={(e) => {
                const data = JSON.parse(JSON.stringify(this.state.gymTypelist));
                data[index].rechargeAmount = e*100;
                this.setState({ gymTypelist: data });
              }}
            />
            <span className="mL10" style={{ paddingLeft: 6 }}>
              元
            </span>
          </div>
        ),
      },
      {
        title: '是否有赠送金额',
        dataIndex: 'enableGift',
        render: (text, record, index) => (
          <Switch
            checked={text == 1 ? true : false}
            onChange={(e) => {
              const data = JSON.parse(JSON.stringify(this.state.gymTypelist));
              data[index].enableGift = e ? 1 : 0;
              this.setState({ gymTypelist: data });
            }}
          />
        ),
      },

      {
        title: '赠送金额',
        dataIndex: 'giftAmount',
        render: (text, record, index) => (
          <div>
            {record.enableGift == 1 && (
              <div>
                <InputNumber
                  value={text/100}
                  onChange={(e) => {
                    const data = JSON.parse(JSON.stringify(this.state.gymTypelist));
                    data[index].giftAmount = e*100;
                    this.setState({ gymTypelist: data });
                  }}
                />
                <span className="mL10" style={{ paddingLeft: 6 }}>
                  元
                </span>
              </div>
            )}
          </div>
        ),
      },
      {
        title: '次数限制（为0不限制）',
        dataIndex: 'rechargeCount',
        render: (text, record, index) => (
          <InputNumber
            value={text}
            onChange={(e) => {
              const data = JSON.parse(JSON.stringify(this.state.gymTypelist));
              data[index].rechargeCount = e;
              this.setState({ gymTypelist: data });
            }}
          />
        ),
      },

      {
        title: '操作',
        render: (text, record) => {
          return (
            <div>
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
                onConfirm={() => this.deletes(record.id)}
                // onCancel={cancel}
                okText="是"
                cancelText="否"
              >
                <span className="mL15 red">删除</span>
              </Popconfirm>
            </div>
          );
        },
      },
    ];

    return (
      <Spin spinning={this.state.spinning}>
        <div style={{ backgroundColor: '#fff', minHeight: 700, paddingRight: 24 }}>
          <h1 style={{ fontWeight: '600', fontSize: '18px', marginBottom: 20 }}>充值活动</h1>
          <Alert
            message="按住鼠标拖拽可调整展示顺序"
            type="warning"
            showIcon
            style={{ marginBottom: 10, marginTop: 20 }}
          />

          <DndProvider backend={HTML5Backend}>
            <Table
              columns={columns}
              rowKey="id"
              rowSelection={rowSelection}
              dataSource={this.state.gymTypelist}
              components={this.components}
              scroll={{ y: 800 }}
              onRow={(record, index) => ({
                index,
                moveRow: this.moveRow,
              })}
              pagination={false}
            />
          </DndProvider>
          <Button
            type="dashed"
            style={{ marginTop: 30, width: '100%' }}
            onClick={() => {
              const data = JSON.parse(JSON.stringify(this.state.gymTypelist));
              data.push({ sort: data.length + 1, enableGift: 0 });
              this.setState({ gymTypelist: data });
            }}
          >
            +添加
          </Button>
          <Button type="primary" onClick={this.submit} style={{ marginTop: 20 }}>
            保存
          </Button>
        </div>

        {this.state.AddRecharge && (
          <AddRecharge
            handleOk={this.handleOk}
            getData={this.getData}
            info={this.state.info}
            useStatus={this.state.useStatus}
          />
        )}
      </Spin>
    );
  }
}

// 7
// 绑定到本页面，此处就可以拿到请求的值，
// allModels所有models集合，mapping是具体的model的命名空间，mappingData命名空间里面你想取的值
export default connect((allModels) => ({}))(DataConnection);
