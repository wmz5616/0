import { post } from '@/utils/request';
import { message, Modal, Tree } from 'antd';
import React from 'react';
const { TreeNode } = Tree;
class App extends React.Component {
  formRef = React.createRef();
  state = {
    expandedKeys: [],
    autoExpandParent: true,
    selectedKeys: [],
    menuList: [],
  };

  componentDidMount() {
    this.getData();
  }

  getData = async () => {
    const res = await post(`/guzhe/role/rule/tree`, {
      searchId: this.props.id,
    });
    if (res && res.code === 10000) {
      this.setState({
        menuList: res.data,
        checkedKeysAll: this.selectAllNodesd(res.data),
        checkedKeys: this.selectAllNode(res.data),
      });
    } else {
      message.error(res?.msg);
    }
  };

  handleOk = async () => {
    const res = await post(`/guzhe/role/rule/update`, {
      roleId: this.props.id,
      ruleIds: this.state.checkedKeys,
    });
    if (res && res.code === 10000) {
      message.success(res.msg);
      this.props.handleOk();
      location.reload();  
    } else {
      message.info(res?.msg);
    }
  };

  handleCancel = () => {
    const { handleOk } = this.props;
    handleOk();
  };

  onExpand = (expandedKeys) => {
    console.log(expandedKeys);
    this.setState({
      expandedKeys,
    });
  };

  onSelect = (selectedKeys, info) => {
    console.log(info.node);
    this.setState(
      {
        selectedKeys,
        departmentId: info.node.key,
        subjectName: info.node.name,
      },
      () => {
        this.getData();
      },
    );
  };

  onCheck = (checkedKeys) => {
    this.setState({ checkedKeys: checkedKeys });
  };

  selectAllNodesd = (data, e) => {
    const a = e ? e : [];
    data.map((res) => {
      a.push(String(res.id));
      if (res.children && res.children.length != 0) {
        this.selectAllNodesd(res.children, a);
      }
    });
    return a;
  };

  selectAllNode = (data, e) => {
    const a = e ? e : [];
    data.map((res) => {
      if (res.isSelected) {
        a.push(String(res.id));
      }
      if (res.children.length != 0) {
        this.selectAllNode(res.children, a);
      }
    });
    return a;
  };

  renderTreeNodes = (data) =>
    data.map((item) => {
      // console.log(data)
      if (item.children) {
        return (
          <TreeNode title={item.ruleName} key={String(item.id)} dataRef={item}>
            {this.renderTreeNodes(item.children)}
          </TreeNode>
        );
      }
      return (
        <TreeNode key={item.id} title={<div>{item.name}</div>} {...item} />
      );
    });

  render() {
    const { lists = [], checkedKeys } = this.state;
    return (
      <>
        <Modal
          style={{ minWidth: '40%' }}
          title="授权"
          visible
          onClose={this.handleCancel}
          onCancel={this.handleCancel}
          onOk={this.handleOk}
        >
          <div
            onClick={() => {
              this.setState({
                checkedKeys: this.state.isCheckAll
                  ? []
                  : this.state.checkedKeysAll,
                isCheckAll: !this.state.isCheckAll,
              });
            }}
            className="clickFont"
            style={{ fontSize: 16, marginLeft: 25 }}
          >
            全选
          </div>
          {this.state.menuList.length != 0 && (
            <Tree
              checkable
              // checkStrictly={true}
              showIcon
              style={{ marginTop: 15, maxHeight: 620, overflowY: 'auto' }}
              showLine
              defaultExpandAll
              onCheck={(e) => this.onCheck(e, 'checkedKeys')}
              checkedKeys={this.state.checkedKeys}
            >
              {this.renderTreeNodes(this.state.menuList)}
            </Tree>
          )}
        </Modal>
      </>
    );
  }
}

export default App;
