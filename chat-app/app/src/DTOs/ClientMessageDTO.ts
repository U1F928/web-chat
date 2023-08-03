export class ClientMessageDTO
{
    private text: string;

    constructor(text: string)
    {
        this.text = text;
    }

    getText(): string
    {
        return this.text;
    }
}
