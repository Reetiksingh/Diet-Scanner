import { Client } from '@stomp/stompjs'
import SockJS from 'sockjs-client'

export function connectNotifications(userId: string, onMessage: (payload: unknown) => void) {
  const client = new Client({
    webSocketFactory: () => new SockJS('/ws/notifications'),
    reconnectDelay: 5000,
    onConnect: () => {
      client.subscribe(`/topic/notifications/${userId}`, (message) => onMessage(JSON.parse(message.body)))
    }
  })
  client.activate()
  return () => client.deactivate()
}

