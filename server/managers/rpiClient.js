

const rpiClient = (id, position, num_of_devices) => {
    this.state = {
        id: id,
        position: position,
        num_of_device: num_of_devices,
        timestamp: Date.now(),
        warning_level: 0,
        height: 0,
    }
    
    this.updateInfo = (params) => {
        for (k of Object.keys(params)) {
            if (this.state[k]) {
                this.state[k] = params[k];
            }
        }
    }

    this.printInfo = () => {
        const result = `id: ${this.state.id}\n'position: ${this.state.position}\
        \nnumber of devices: ${this.state.num_of_device}\ntimestamp: ${this.state.timestamp}\
        \nwarning_level: ${this.state.warning_level}\nheight: ${this.state.height}`
    }

    this.getState = () => {
        return Object.assign({}, this.state);       // deep copy
    }

};


module.exports = rpiClient;

