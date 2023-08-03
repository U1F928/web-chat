export interface ChatMessageJSON 
{
  id: number;
  roomName: string;
  creationTimestamp: number;
  text: string;
}

export class ChatMessageDTO
{
    private id: number;
    private roomName: string;
    private creationTimestamp: number;
    private text: string;

    constructor(id: number, roomName: string, creationTimestamp: number, text: string)
    {
        this.id = id;
        this.roomName = roomName;
        this.creationTimestamp = creationTimestamp;
        this.text = text;
    }

    getID(): number
    {
        return this.id;
    }

    getText(): string
    {
        return this.text;
    }

    getRoomName(): string
    {
        return this.roomName;
    }

    getCreationTimestamp(): number
    {
        return this.creationTimestamp;
    }

    static fromJSON(json: ChatMessageJSON): ChatMessageDTO
    {
        const newChatMessageDTO = new ChatMessageDTO(json.id, json.roomName, json.creationTimestamp, json.text);
        return newChatMessageDTO;
    }
}
