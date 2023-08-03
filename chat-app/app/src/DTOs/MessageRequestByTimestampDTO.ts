import { MessageRequestByTimestampType } from "./MessageRequestByTimestampType";

export class MessageRequestByTimestampDTO
{
    private creationTimestamp: number;
    private requestType: MessageRequestByTimestampType;
    private pageSize: number;
    private pageNumber: number;

    constructor(
        creationTimestamp: number,
        requestType: MessageRequestByTimestampType,
        pageSize: number,
        pageNumber: number
    )
    {
        this.creationTimestamp = creationTimestamp;
        this.requestType = requestType;
        this.pageSize = pageSize;
        this.pageNumber = pageNumber;
    }

    getCreationTimestamp(): number
    {
        return this.creationTimestamp;
    }

    getRequestType(): MessageRequestByTimestampType
    {
        return this.requestType;
    }

    getPageSize(): number
    {
        return this.pageSize;
    }

    getPageNumber(): number
    {
        return this.pageNumber;
    }
}
