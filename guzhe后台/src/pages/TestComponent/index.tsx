import React from 'react';

// 定义Props类型
interface AppProps {
  title: string;
  version?: string; // 可选属性
}

// 定义State类型
interface AppState {
  count: number;
  message: string;
}

// Class组件继承React.Component<Props, State>
class App extends React.Component<AppProps, AppState> {
  // 初始化state
  state: AppState = {
    count:11,
    message: 'Hello React with TypeScript!'
  };

  // 构造函数（可选）
  constructor(props: AppProps) {
    super(props);
    // 初始化state也可以放在这里
    // this.state = {
    //   count: 0,
    //   message: 'Hello React with TypeScript!'
    // };
  }

  // 事件处理函数
  handleClick = () => {
    this.setState({ count: this.state.count + 1 });
  };

  // 条件渲染函数
  renderMessage = () => {
    if (this.state.count > 5) {
      return <p className="text-blue-600">Count is greater than 5!</p>;
    }
    return <p className="text-green-600">{this.state.message}</p>;
  };

  // 生命周期方法
  componentDidMount() {
    console.log('Component mounted');
  }

  componentDidUpdate(prevProps: AppProps, prevState: AppState) {
    if (prevState.count !== this.state.count) {
      console.log(`Count updated from ${prevState.count} to ${this.state.count}`);
    }
  }

  // 渲染方法
  render() {
    const { title, version = '1.0.0' } = this.props;
    
    return (
      <div className="app-container p-6 max-w-md mx-auto bg-white rounded-xl shadow-md space-y-4">
        <h1 className="text-2xl font-bold text-gray-800">{title}</h1>
        <p className="text-gray-600">Version: {version}</p>
        
        <div className="counter-section">
          <p className="text-lg">Count: <span className="font-semibold">{this.state.count}</span></p>
          <button 
            onClick={this.handleClick}
            className="mt-2 px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600 transition-colors"
          >
            Increment
          </button>
        </div>
        
        <div className="message-section">
          {this.renderMessage()}
        </div>
        
        <div className="info-section text-sm text-gray-500">
          <p>This is a React Class Component written in TypeScript.</p>
          <ul className="list-disc list-inside mt-2">
            <li>Strongly typed props and state</li>
            <li>Autocomplete support</li>
            <li>Compile-time error checking</li>
          </ul>
        </div>
      </div>
    );
  }
}

export default App;